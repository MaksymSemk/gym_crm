package com.example.trainerworkloadservice.workload.kafka;

import com.example.trainerworkloadservice.logging.TransactionLoggingFilter;
import com.example.trainerworkloadservice.workload.TrainerWorkloadService;
import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadConsumer {

    private final TrainerWorkloadService workloadService;
    private final Validator validator;

    @KafkaListener(
            topics = "${kafka.topic.trainer-workload:trainer-workload-topic}",
            groupId = "${spring.kafka.consumer.group-id:trainer-workload-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload TrainerWorkloadRequestDto payload,
            @Header(name = TransactionLoggingFilter.TRANSACTION_ID_HEADER, required = false) byte[] rawTxIdBytes,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String key,
            @Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
            @Header(value = KafkaHeaders.OFFSET, required = false) Long offset
    ) {
        String txId = extractTransactionId(rawTxIdBytes);

        MDC.put(TransactionLoggingFilter.MDC_TRANSACTION_ID_KEY, txId);
        try {
            log.info("Received Kafka message from partition {} offset {} with key: {}", partition, offset, key);

            Set<ConstraintViolation<TrainerWorkloadRequestDto>> violations = validator.validate(payload);
            if (!violations.isEmpty()) {
                String errorMsg = violations.stream()
                        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                        .collect(Collectors.joining(", "));
                log.error("Validation failed for workload message: {}", errorMsg);
                throw new ValidationException("Invalid message payload: " + errorMsg);
            }

            workloadService.processWorkload(payload);
            log.info("Successfully processed Kafka workload update for trainer: {}", payload.trainerUsername());
        } finally {
            MDC.remove(TransactionLoggingFilter.MDC_TRANSACTION_ID_KEY);
        }
    }

    private String extractTransactionId(byte[] rawTxIdBytes) {
        return (rawTxIdBytes != null && rawTxIdBytes.length > 0)
                ? new String(rawTxIdBytes, StandardCharsets.UTF_8)
                : UUID.randomUUID().toString();
    }
}