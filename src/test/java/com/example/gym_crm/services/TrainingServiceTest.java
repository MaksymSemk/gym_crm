package com.example.gym_crm.services;

import com.example.gym_crm.common.exception.EntityAlreadyExistsException;
import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.trainee.TraineeRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerRepository;
import com.example.gym_crm.trainer.TrainingDoesNotBelongToTrainerException;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingRepository;
import com.example.gym_crm.training.TrainingServiceImpl;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    private TrainingServiceImpl trainingService;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    private TrainingId trainingId;
    private TrainingType yogaType;
    private TrainingType cardioType;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingServiceImpl();
        trainingService.setTrainingRepository(trainingRepository);
        trainingService.setTrainingTypeRepository(trainingTypeRepository);
        trainingService.setTrainerRepository(trainerRepository);
        trainingService.setTraineeRepository(traineeRepository);

        trainingId = new TrainingId(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now());

        yogaType = new TrainingType();
        yogaType.setId("Yoga");

        cardioType = new TrainingType();
        cardioType.setId("Cardio");
    }

    @Nested
    @DisplayName("Create Training Tests")
    class CreateTrainingTests {

        @Test
        @DisplayName("Should create training successfully when all references and specializations match")
        void createTraining_Success() {
            TrainingCreateDto dto = new TrainingCreateDto(trainingId, "Morning Yoga", Set.of(yogaType), LocalTime.of(1, 0));
            Trainer mockTrainer = new Trainer("Jane", "Doe", "jane.doe", "pass", true, Set.of(yogaType));

            when(trainingRepository.existsById(trainingId)).thenReturn(false);
            when(traineeRepository.existsById(trainingId.traineeId())).thenReturn(true);
            when(trainerRepository.findById(trainingId.trainerId())).thenReturn(Optional.of(mockTrainer));
            when(trainingTypeRepository.existsById("Yoga")).thenReturn(true);
            when(trainingRepository.create(any(Training.class))).thenAnswer(inv -> inv.getArgument(0));

            Training result = trainingService.createTraining(dto);

            assertNotNull(result);
            assertEquals("Morning Yoga", result.getTrainingName());
            assertEquals(trainingId, result.getId());
            verify(trainingRepository, times(1)).create(any(Training.class));
        }

        @Test
        @DisplayName("Should throw EntityAlreadyExistsException when primary composite key exists")
        void createTraining_Collision_ThrowsException() {
            TrainingCreateDto dto = new TrainingCreateDto(trainingId, "Morning Yoga", Set.of(yogaType), LocalTime.of(1, 0));
            when(trainingRepository.existsById(trainingId)).thenReturn(true);

            assertThrows(EntityAlreadyExistsException.class, () -> trainingService.createTraining(dto));
            verify(trainingRepository, never()).create(any());
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when referenced trainee is missing")
        void createTraining_TraineeNotFound_ThrowsException() {
            TrainingCreateDto dto = new TrainingCreateDto(trainingId, "Morning Yoga", Set.of(yogaType), LocalTime.of(1, 0));

            when(trainingRepository.existsById(trainingId)).thenReturn(false);
            when(traineeRepository.existsById(trainingId.traineeId())).thenReturn(false);

            assertThrows(EntityDoesNotExistException.class, () -> trainingService.createTraining(dto));
        }

        @Test
        @DisplayName("Should throw TrainingDoesNotBelongToTrainerException when trainer lacks matching specialization")
        void createTraining_TrainerLacksSpecialization_ThrowsException() {
            TrainingCreateDto dto = new TrainingCreateDto(trainingId, "Morning Yoga", Set.of(yogaType), LocalTime.of(1, 0));
            // Trainer is only specialized in Cardio, not Yoga
            Trainer mockTrainer = new Trainer("Jane", "Doe", "jane.doe", "pass", true, Set.of(cardioType));

            when(trainingRepository.existsById(trainingId)).thenReturn(false);
            when(traineeRepository.existsById(trainingId.traineeId())).thenReturn(true);
            when(trainerRepository.findById(trainingId.trainerId())).thenReturn(Optional.of(mockTrainer));
            when(trainingTypeRepository.existsById("Yoga")).thenReturn(true);

            assertThrows(TrainingDoesNotBelongToTrainerException.class, () -> trainingService.createTraining(dto));
        }
    }

    @Nested
    @DisplayName("Get Training Tests")
    class GetTrainingTests {

        @Test
        @DisplayName("Should return training mapping when ID is resolved")
        void getTraining_Success() {
            Training expected = new Training();
            expected.setId(trainingId);

            when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(expected));

            Training result = trainingService.getTraining(trainingId);

            assertNotNull(result);
            assertEquals(trainingId, result.getId());
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when ID does not match any stored records")
        void getTraining_NotFound_ThrowsException() {
            when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

            assertThrows(EntityDoesNotExistException.class, () -> trainingService.getTraining(trainingId));
        }
    }
}