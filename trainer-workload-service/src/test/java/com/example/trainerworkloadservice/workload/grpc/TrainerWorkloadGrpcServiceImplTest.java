package com.example.trainerworkloadservice.workload.grpc;

import com.example.grpc.workload.ActionType;
import com.example.grpc.workload.WorkloadRequest;
import com.example.grpc.workload.WorkloadResponse;
import com.example.trainerworkloadservice.workload.TrainerWorkloadService;
import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadGrpcServiceImplTest {

    @Mock
    private TrainerWorkloadService workloadService;

    @Mock
    private StreamObserver<WorkloadResponse> responseObserver;

    @InjectMocks
    private TrainerWorkloadGrpcServiceImpl grpcService;

    @Test
    @DisplayName("updateWorkload converts proto, calls service and completes observer")
    void updateWorkload_ValidRequest_Success() {
        WorkloadRequest request = WorkloadRequest.newBuilder()
                .setTrainerUsername("sarah.connor")
                .setTrainerFirstName("Sarah")
                .setTrainerLastName("Connor")
                .setIsActive(true)
                .setTrainingDate("2026-08-20")
                .setTrainingDuration(90)
                .setActionType(ActionType.DELETE)
                .build();

        grpcService.updateWorkload(request, responseObserver);

        ArgumentCaptor<TrainerWorkloadRequestDto> dtoCaptor = ArgumentCaptor.forClass(TrainerWorkloadRequestDto.class);
        verify(workloadService).processWorkload(dtoCaptor.capture());

        TrainerWorkloadRequestDto captured = dtoCaptor.getValue();
        assertThat(captured.trainerUsername()).isEqualTo("sarah.connor");
        assertThat(captured.trainingDate()).isEqualTo(LocalDate.of(2026, 8, 20));
        assertThat(captured.trainingDuration()).isEqualTo(90);
        assertThat(captured.actionType()).isEqualTo(com.example.trainerworkloadservice.workload.dto.ActionType.DELETE);

        ArgumentCaptor<WorkloadResponse> responseCaptor = ArgumentCaptor.forClass(WorkloadResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();

        WorkloadResponse response = responseCaptor.getValue();
        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Workload updated successfully");
    }
}