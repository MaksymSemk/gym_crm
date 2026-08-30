package com.example.gym_crm.training.remote;

import com.example.gym_crm.training.remote.dto.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "trainer-workload-service",
        path = "/api/v1/workload",
        fallbackFactory = TrainerWorkloadClientFallbackFactory.class
)public interface TrainerWorkloadClient {

    @PostMapping
    ResponseEntity<Void> updateWorkload(@RequestBody TrainerWorkloadRequest request);
}