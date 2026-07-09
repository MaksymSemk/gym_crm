package com.example.gym_crm.training;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.repository.TrainingRepository;
import com.example.gym_crm.training_type.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    private Trainee sampleTrainee;
    private Trainer sampleTrainer;
    private TrainingType sampleSpecialization;
    private Training sampleTraining;
    private UUID trainingId;

    @BeforeEach
    void setUp() {
        trainingId = UUID.randomUUID();

        sampleTrainee = new Trainee();
        sampleTrainee.setId(UUID.randomUUID());

        sampleSpecialization = new TrainingType();
        sampleSpecialization.setId(1L);
        sampleSpecialization.setName("Yoga");

        sampleTrainer = new Trainer();
        sampleTrainer.setId(UUID.randomUUID());
        sampleTrainer.setSpecialization(sampleSpecialization);

        sampleTraining = Training.builder()
                .id(trainingId)
                .trainee(sampleTrainee)
                .trainer(sampleTrainer)
                .trainingType(sampleSpecialization)
                .trainingName("Morning Flow")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();
    }

    @Nested
    @DisplayName("Create Training Tests")
    class CreateTrainingTests {

        @Test
        @DisplayName("Should successfully resolve entities and save new training session")
        void createTraining_Success() {
            TrainingCreateDto dto = mock(TrainingCreateDto.class);
            when(dto.getTraineeUsername()).thenReturn("john.trainee");
            when(dto.getTrainerUsername()).thenReturn("emma.trainer");
            when(dto.getTrainingName()).thenReturn("Morning Flow");
            when(dto.getTrainingDate()).thenReturn(LocalDate.now());
            when(dto.getTrainingDuration()).thenReturn(60);

            when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(sampleTrainee));
            when(trainerRepository.findByUserUsername("emma.trainer")).thenReturn(Optional.of(sampleTrainer));
            when(trainingRepository.save(any(Training.class))).thenReturn(sampleTraining);

            Training result = trainingService.createTraining(dto);

            assertNotNull(result);
            assertEquals("Morning Flow", result.getTrainingName());
            assertEquals(sampleSpecialization, result.getTrainingType());
            verify(trainingRepository, times(1)).save(any(Training.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when creation parameter DTO is null")
        void createTraining_NullDto_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> trainingService.createTraining(null));
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when trainee username lookup fails")
        void createTraining_TraineeNotFound_ThrowsException() {
            TrainingCreateDto dto = mock(TrainingCreateDto.class);
            when(dto.getTraineeUsername()).thenReturn("ghost.trainee");

            when(traineeRepository.findByUserUsername("ghost.trainee")).thenReturn(Optional.empty());

            assertThrows(EntityDoesNotExistException.class, () -> trainingService.createTraining(dto));
            verifyNoInteractions(trainerRepository, trainingRepository);
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when trainer username lookup fails")
        void createTraining_TrainerNotFound_ThrowsException() {
            TrainingCreateDto dto = mock(TrainingCreateDto.class);
            when(dto.getTraineeUsername()).thenReturn("john.trainee");
            when(dto.getTrainerUsername()).thenReturn("ghost.trainer");

            when(traineeRepository.findByUserUsername("john.trainee")).thenReturn(Optional.of(sampleTrainee));
            when(trainerRepository.findByUserUsername("ghost.trainer")).thenReturn(Optional.empty());

            assertThrows(EntityDoesNotExistException.class, () -> trainingService.createTraining(dto));
            verify(trainingRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Training Tests")
    class GetTrainingTests {

        @Test
        @DisplayName("Should return training mapping entity when a valid match is found in storage")
        void getTraining_Success() {
            when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(sampleTraining));

            Training result = trainingService.getTraining(trainingId);

            assertNotNull(result);
            assertEquals(trainingId, result.getId());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when query identification parameter is null")
        void getTraining_NullId_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> trainingService.getTraining(null));
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when specified primary identification key is missing")
        void getTraining_NotFound_ThrowsException() {
            when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

            assertThrows(EntityDoesNotExistException.class, () -> trainingService.getTraining(trainingId));
        }
    }
}