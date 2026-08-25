package com.example.trainerworkloadservice.workload;

import com.example.trainerworkloadservice.workload.dto.ActionType;
import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
import com.example.trainerworkloadservice.workload.model.MonthSummary;
import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import com.example.trainerworkloadservice.workload.model.YearSummary;
import com.example.trainerworkloadservice.workload.repository.TrainerWorkloadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceTest {

    @Mock
    private TrainerWorkloadRepository repository;

    @InjectMocks
    private TrainerWorkloadService workloadService;

    private TrainerWorkloadRequestDto baseRequest;

    @BeforeEach
    void setUp() {
        baseRequest = new TrainerWorkloadRequestDto(
                "john.doe",
                "John",
                "Doe",
                true,
                LocalDate.of(2026, 8, 15),
                60,
                ActionType.ADD
        );
    }

    @Test
    @DisplayName("processWorkload creates new TrainerWorkload when trainer does not exist")
    void processWorkload_NewTrainer_CreatesAndSaves() {
        when(repository.findByUsername("john.doe")).thenReturn(Optional.empty());

        workloadService.processWorkload(baseRequest);

        ArgumentCaptor<TrainerWorkload> captor = ArgumentCaptor.forClass(TrainerWorkload.class);
        verify(repository).save(captor.capture());

        TrainerWorkload saved = captor.getValue();
        assertThat(saved.getTrainerUsername()).isEqualTo("john.doe");
        assertThat(saved.getTrainerFirstName()).isEqualTo("John");
        assertThat(saved.getTrainerLastName()).isEqualTo("Doe");
        assertThat(saved.getTrainerStatus()).isTrue();
        assertThat(saved.getYears()).hasSize(1);

        YearSummary yearSummary = saved.getYears().get(0);
        assertThat(yearSummary.getYear()).isEqualTo(2026);
        assertThat(yearSummary.getMonths()).hasSize(1);

        MonthSummary monthSummary = yearSummary.getMonths().get(0);
        assertThat(monthSummary.getMonthNumber()).isEqualTo(8);
        assertThat(monthSummary.getTrainingSummaryDuration()).isEqualTo(60);
    }

    @Test
    @DisplayName("processWorkload increments duration on ADD for existing month")
    void processWorkload_ExistingMonth_AddsDuration() {
        TrainerWorkload existing = new TrainerWorkload(
                "john.doe", "John", "Doe", true,
                new ArrayList<>(List.of(
                        new YearSummary(2026, new ArrayList<>(List.of(new MonthSummary(8, 45))))
                ))
        );
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        workloadService.processWorkload(baseRequest);

        verify(repository).save(existing);
        assertThat(existing.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration()).isEqualTo(105);
    }

    @Test
    @DisplayName("processWorkload decrements duration on DELETE action")
    void processWorkload_DeleteAction_DecrementsDuration() {
        TrainerWorkload existing = new TrainerWorkload(
                "john.doe", "John", "Doe", true,
                new ArrayList<>(List.of(
                        new YearSummary(2026, new ArrayList<>(List.of(new MonthSummary(8, 100))))
                ))
        );
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        TrainerWorkloadRequestDto deleteRequest = new TrainerWorkloadRequestDto(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 15), 40, ActionType.DELETE
        );

        workloadService.processWorkload(deleteRequest);

        verify(repository).save(existing);
        assertThat(existing.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration()).isEqualTo(60);
    }

    @Test
    @DisplayName("processWorkload bounds duration to zero when DELETE exceeds current value")
    void processWorkload_DeleteAction_DoesNotGoBelowZero() {
        TrainerWorkload existing = new TrainerWorkload(
                "john.doe", "John", "Doe", true,
                new ArrayList<>(List.of(
                        new YearSummary(2026, new ArrayList<>(List.of(new MonthSummary(8, 30))))
                ))
        );
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        TrainerWorkloadRequestDto deleteRequest = new TrainerWorkloadRequestDto(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 15), 50, ActionType.DELETE
        );

        workloadService.processWorkload(deleteRequest);

        verify(repository).save(existing);
        assertThat(existing.getYears().get(0).getMonths().get(0).getTrainingSummaryDuration()).isZero();
    }

    @Test
    @DisplayName("getTrainerWorkload returns workload when trainer exists")
    void getTrainerWorkload_Found_ReturnsWorkload() {
        TrainerWorkload existing = new TrainerWorkload("john.doe", "John", "Doe", true, new ArrayList<>());
        when(repository.findByUsername("john.doe")).thenReturn(Optional.of(existing));

        TrainerWorkload result = workloadService.getTrainerWorkload("john.doe");

        assertThat(result).isSameAs(existing);
    }

    @Test
    @DisplayName("getTrainerWorkload throws IllegalArgumentException when trainer not found")
    void getTrainerWorkload_NotFound_ThrowsException() {
        when(repository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workloadService.getTrainerWorkload("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Trainer not found with username: unknown");
    }
}