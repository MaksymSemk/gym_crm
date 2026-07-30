package com.example.gym_crm.common.health;

import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.repository.TrainingRepository;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EntityCountHealthIndicatorTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private EntityCountHealthIndicator healthIndicator;

    @Test
    @DisplayName("Should return Status.UP with exact entity counts when repositories respond successfully")
    void health_Success() {
        when(userRepository.count()).thenReturn(10L);
        when(traineeRepository.count()).thenReturn(6L);
        when(trainerRepository.count()).thenReturn(4L);
        when(trainingRepository.count()).thenReturn(20L);
        when(trainingTypeRepository.count()).thenReturn(3L);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(10L, health.getDetails().get("users"));
        assertEquals(6L, health.getDetails().get("trainees"));
        assertEquals(4L, health.getDetails().get("trainers"));
        assertEquals(20L, health.getDetails().get("trainings"));
        assertEquals(3L, health.getDetails().get("trainingTypes"));
    }

    @Test
    @DisplayName("Should return Status.DOWN when any repository throws exception")
    void health_DatabaseFailure_ReturnsDown() {
        when(userRepository.count()).thenThrow(new RuntimeException("Database connection timeout"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Failed to query database entity counts", health.getDetails().get("error"));
        assertEquals("Database connection timeout", health.getDetails().get("message"));
    }
}