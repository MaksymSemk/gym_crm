package com.example.trainerworkloadservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<Object, Object> dlqProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        // Key serializer handling Object / String keys
        Serializer<Object> keySerializer = (topic, data) -> {
            if (data == null) {
                return null;
            }
            if (data instanceof byte[] bytes) {
                return bytes;
            }
            return data.toString().getBytes(StandardCharsets.UTF_8);
        };

        // Value serializer handling raw bytes, strings, or POJOs/records
        Serializer<Object> valueSerializer = (topic, data) -> {
            if (data == null) {
                return null;
            }
            if (data instanceof byte[] bytes) {
                return bytes;
            }
            if (data instanceof String str) {
                return str.getBytes(StandardCharsets.UTF_8);
            }
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (Exception e) {
                log.error("Failed to serialize DLQ message payload", e);
                return data.toString().getBytes(StandardCharsets.UTF_8);
            }
        };

        return new DefaultKafkaProducerFactory<>(
                props,
                keySerializer,
                valueSerializer
        );
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