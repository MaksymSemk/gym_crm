package com.example.gym_crm.training.remote;

import com.example.gym_crm.training.remote.dto.TrainerWorkloadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrainerWorkloadClientFallbackFactory implements FallbackFactory<TrainerWorkloadClient> {

    @Override
    public TrainerWorkloadClient create(Throwable cause) {
        return new TrainerWorkloadClient() {
            @Override
            public ResponseEntity<Void> updateWorkload(TrainerWorkloadRequest request) {
                log.error("Fallback triggered for workload service call. Error: {}", cause.getMessage());
                // Fallback action: log failure or queue for retry without crashing training creation
                return ResponseEntity.internalServerError().build();
            }
        };
    }
}