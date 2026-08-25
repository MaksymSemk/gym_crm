package com.example.trainerworkloadservice.workload.grpc;

import com.example.grpc.workload.TrainerWorkloadGrpcServiceGrpc;
import com.example.grpc.workload.WorkloadRequest;
import com.example.grpc.workload.WorkloadResponse;
import com.example.trainerworkloadservice.logging.GrpcServerLoggingInterceptor;
import com.example.trainerworkloadservice.workload.TrainerWorkloadService;
import com.example.trainerworkloadservice.workload.dto.ActionType;
import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadGrpcServiceImpl extends TrainerWorkloadGrpcServiceGrpc.TrainerWorkloadGrpcServiceImplBase {

    private final TrainerWorkloadService workloadService;

    @Override
    public void updateWorkload(WorkloadRequest request, StreamObserver<WorkloadResponse> responseObserver) {
        log.debug("Operation: Mapping protobuf WorkloadRequest to internal DTO");
        TrainerWorkloadRequestDto dto = getDto(request);

        workloadService.processWorkload(dto);

        WorkloadResponse response = WorkloadResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Workload updated successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private static @NonNull TrainerWorkloadRequestDto getDto(WorkloadRequest request) {
        ActionType action = request.getActionType() == com.example.grpc.workload.ActionType.DELETE
                ? ActionType.DELETE
                : ActionType.ADD;

        TrainerWorkloadRequestDto dto = new TrainerWorkloadRequestDto(
                request.getTrainerUsername(),
                request.getTrainerFirstName(),
                request.getTrainerLastName(),
                request.getIsActive(),
                LocalDate.parse(request.getTrainingDate()),
                request.getTrainingDuration(),
                action
        );
        return dto;
    }
}