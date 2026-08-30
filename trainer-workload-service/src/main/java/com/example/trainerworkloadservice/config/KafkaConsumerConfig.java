package com.example.trainerworkloadservice.config;

import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:trainer-workload-group}")
    private String groupId;

    // --- Consumer Configuration ---

    @Bean
    public ConsumerFactory<String, TrainerWorkloadRequestDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ObjectMapper customMapper = objectMapper.copy()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Deserializer<TrainerWorkloadRequestDto> valueDeserializer = (topic, data) -> {
            if (data == null || data.length == 0) {
                return null;
            }
            try {
                return customMapper.readValue(data, TrainerWorkloadRequestDto.class);
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize JSON to TrainerWorkloadRequestDto: " + new String(data, StandardCharsets.UTF_8), e);
            }
        };

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(valueDeserializer)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TrainerWorkloadRequestDto> kafkaListenerContainerFactory(
            ConsumerFactory<String, TrainerWorkloadRequestDto> consumerFactory,
            DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TrainerWorkloadRequestDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    // --- DLQ Producer Configuration ---

    @Bean
    public ProducerFactory<Object, Object> dlqProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        Serializer<Object> keySerializer = (topic, data) -> {
            if (data == null) return null;
            if (data instanceof byte[] bytes) return bytes;
            return data.toString().getBytes(StandardCharsets.UTF_8);
        };

        Serializer<Object> valueSerializer = (topic, data) -> {
            if (data == null) return null;
            if (data instanceof byte[] bytes) return bytes;
            if (data instanceof String str) return str.getBytes(StandardCharsets.UTF_8);
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                log.error("Failed to serialize DLQ message payload", e);
                return data.toString().getBytes(StandardCharsets.UTF_8);
            }
        };

        return new DefaultKafkaProducerFactory<>(props, keySerializer, valueSerializer);
    }

    @Bean
    public KafkaTemplate<Object, Object> dlqKafkaTemplate(ProducerFactory<Object, Object> dlqProducerFactory) {
        return new KafkaTemplate<>(dlqProducerFactory);
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaOperations<Object, Object> dlqKafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(dlqKafkaTemplate,
                (record, ex) -> {
                    log.error("Routing failed message to DLQ [Topic: {}, Key: {}, Cause: {}]",
                            record.topic() + ".DLT", record.key(), ex.getMessage());
                    return new TopicPartition(record.topic() + ".DLT", -1);
                });

        FixedBackOff backOff = new FixedBackOff(1000L, 2L);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        errorHandler.addNotRetryableExceptions(
                jakarta.validation.ValidationException.class,
                IllegalArgumentException.class
        );

        return errorHandler;
    }
}