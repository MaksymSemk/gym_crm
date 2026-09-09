package com.example.gym_crm.training.remote.kafka;

import com.example.gym_crm.common.logging.TransactionContextStorage;
import com.example.gym_crm.training.remote.dto.TrainerWorkloadRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadProducer {

    private final KafkaTemplate<String, TrainerWorkloadRequest> kafkaTemplate;
    private final TransactionContextStorage contextStorage;

    @Value("${kafka.topic.trainer-workload:trainer-workload-topic}")
    private String topicName;

    public void sendWorkloadUpdate(TrainerWorkloadRequest request) {
        log.debug("Publishing workload update to Kafka topic '{}' for trainer: {}", topicName, request.trainerUsername());

        ProducerRecord<String, TrainerWorkloadRequest> record =
                new ProducerRecord<>(topicName, request.trainerUsername(), request);

        String txId = contextStorage.getTransactionId();
        if (txId != null && !txId.isBlank()) {
            record.headers().add(new RecordHeader(TransactionContextStorage.TRANSACTION_ID_HEADER, txId.getBytes(StandardCharsets.UTF_8)));
        }

        kafkaTemplate.send(record).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to deliver message to Kafka topic '{}' for trainer: {}", topicName, request.trainerUsername(), ex);
            } else {
                log.info("Message successfully sent to topic '{}' partition {} offset {}",
                        topicName,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}