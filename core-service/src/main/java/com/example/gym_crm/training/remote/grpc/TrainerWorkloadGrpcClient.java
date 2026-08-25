package com.example.gym_crm.training.remote.grpc;


import com.example.grpc.workload.ActionType;
import com.example.grpc.workload.TrainerWorkloadGrpcServiceGrpc;
import com.example.grpc.workload.WorkloadRequest;
import com.example.grpc.workload.WorkloadResponse;
import com.example.gym_crm.training.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadGrpcClient {

    private final TrainerWorkloadGrpcServiceGrpc.TrainerWorkloadGrpcServiceBlockingStub workloadStub;

    public void deductWorkload(Training training) {
        log.debug("Sending gRPC DELETE workload for trainer {}", training.getTrainer().getUser().getUsername());
        try {
            WorkloadRequest request = WorkloadRequest.newBuilder()
                    .setTrainerUsername(training.getTrainer().getUser().getUsername())
                    .setTrainerFirstName(training.getTrainer().getUser().getFirstName())
                    .setTrainerLastName(training.getTrainer().getUser().getLastName())
                    .setIsActive(training.getTrainer().getUser().getIsActive())
                    .setTrainingDate(training.getTrainingDate().toString())
                    .setTrainingDuration(training.getTrainingDuration())
                    .setActionType(ActionType.DELETE)
                    .build();

            WorkloadResponse response = workloadStub.updateWorkload(request);
            log.info("Workload successfully deducted via gRPC: {}", response.getMessage());
        } catch (Exception e) {
            log.error("Failed to deduct trainer workload via gRPC for training ID: {}", training.getId(), e);
        }
    }
}