package com.example.gym_crm.services;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerRepository;
import com.example.gym_crm.trainer.TrainerServiceImpl;
import com.example.gym_crm.trainer.TrainingDoesNotBelongToTrainerException;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingRepository;
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
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    private TrainerServiceImpl trainerService;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserUtils userUtils;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    private TrainingType strengthTraining;
    private TrainingType cardio;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl();
        trainerService.setTrainerRepository(trainerRepository);
        trainerService.setUserUtils(userUtils);
        trainerService.setTrainingTypeRepository(trainingTypeRepository);
        trainerService.setTrainingRepository(trainingRepository);

        strengthTraining = new TrainingType();
        strengthTraining.setId("Strength Training");

        cardio = new TrainingType();
        cardio.setId("Cardio");
    }

    @Nested
    @DisplayName("Get Trainer By ID Tests")
    class GetTrainerByIdTests {

        @Test
        @DisplayName("Should return trainer profile when requested ID exists")
        void getTrainerById_Success() {
            UUID id = UUID.randomUUID();
            Trainer expectedTrainer = new Trainer();
            expectedTrainer.setId(id);

            when(trainerRepository.findById(id)).thenReturn(Optional.of(expectedTrainer));

            Trainer result = trainerService.getTrainerByID(id);

            assertNotNull(result);
            assertEquals(id, result.getId());
            verify(trainerRepository, times(1)).findById(id);
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when ID is not found")
        void getTrainerById_NotFound_ThrowsException() {
            UUID id = UUID.randomUUID();
            when(trainerRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityDoesNotExistException.class, () -> trainerService.getTrainerByID(id));
        }
    }

    @Nested
    @DisplayName("Create Trainer Tests")
    class CreateTrainerTests {

        @Test
        @DisplayName("Should create trainer successfully when all specializations exist")
        void createTrainer_Success() {
            TrainerCreateDto dto = new TrainerCreateDto("John", "Smith", true, Set.of(strengthTraining, cardio));

            when(userUtils.createUsername("John", "Smith")).thenReturn("John.Smith");
            when(userUtils.generatePassword()).thenReturn("pass123456");
            when(trainingTypeRepository.existsById("Strength Training")).thenReturn(true);
            when(trainingTypeRepository.existsById("Cardio")).thenReturn(true);
            when(trainerRepository.create(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

            Trainer result = trainerService.createTrainer(dto);

            assertNotNull(result);
            assertEquals("John", result.getFirstName());
            assertEquals("Smith", result.getLastName());
            assertEquals("John.Smith", result.getUsername());
            assertEquals("pass123456", result.getPassword());
            assertTrue(result.getIsActive());
            assertEquals(2, result.getSpecialization().size());
            verify(trainerRepository, times(1)).create(any(Trainer.class));
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when any specialization does not exist")
        void createTrainer_InvalidSpecialization_ThrowsException() {
            TrainerCreateDto dto = new TrainerCreateDto("John", "Smith", true, Set.of(cardio));

            when(trainingTypeRepository.existsById("Cardio")).thenReturn(false);

            assertThrows(EntityDoesNotExistException.class, () -> trainerService.createTrainer(dto));
        }
    }

    @Nested
    @DisplayName("Update Trainer Tests")
    class UpdateTrainerTests {

        @Test
        @DisplayName("Should update trainer profile successfully without renaming identity")
        void updateTrainer_Success_NoIdentityChange() {
            UUID id = UUID.randomUUID();
            Trainer existingTrainer = new Trainer("John", "Smith", "John.Smith", "pass", true, Set.of(strengthTraining));
            existingTrainer.setId(id);

            TrainerUpdateDto dto = new TrainerUpdateDto(id, "John", "Smith", false, null, null);

            when(trainerRepository.findById(id)).thenReturn(Optional.of(existingTrainer));
            when(trainerRepository.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

            Trainer result = trainerService.updateTrainer(dto);

            assertNotNull(result);
            assertFalse(result.getIsActive());
            assertEquals("John.Smith", result.getUsername());
            verify(userUtils, never()).createUsername(anyString(), anyString());
        }

        @Test
        @DisplayName("Should update profile and recalculate username when name fields change")
        void updateTrainer_Success_WithIdentityChange() {
            UUID id = UUID.randomUUID();
            Trainer existingTrainer = new Trainer("John", "Smith", "John.Smith", "pass", true, Set.of(strengthTraining));
            existingTrainer.setId(id);

            TrainerUpdateDto dto = new TrainerUpdateDto(id, "Johnny", "Smith", true, null, null);

            when(trainerRepository.findById(id)).thenReturn(Optional.of(existingTrainer));
            when(userUtils.createUsername("Johnny", "Smith")).thenReturn("Johnny.Smith");
            when(trainerRepository.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

            Trainer result = trainerService.updateTrainer(dto);

            assertNotNull(result);
            assertEquals("Johnny.Smith", result.getUsername());
            verify(userUtils, times(1)).createUsername("Johnny", "Smith");
        }

        @Test
        @DisplayName("Should update specialization set when all input specializations are valid")
        void updateTrainer_Success_UpdateSpecialization() {
            UUID id = UUID.randomUUID();
            Trainer existingTrainer = new Trainer("John", "Smith", "John.Smith", "pass", true, Set.of(strengthTraining));
            existingTrainer.setId(id);

            TrainerUpdateDto dto = new TrainerUpdateDto(id, "John", "Smith", true, Set.of(cardio), null);

            when(trainerRepository.findById(id)).thenReturn(Optional.of(existingTrainer));
            when(trainingTypeRepository.existsById("Cardio")).thenReturn(true);
            when(trainerRepository.update(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

            Trainer result = trainerService.updateTrainer(dto);

            assertNotNull(result);
            assertTrue(result.getSpecialization().contains(cardio));
            assertFalse(result.getSpecialization().contains(strengthTraining));
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException during update if target specialization is invalid")
        void updateTrainer_InvalidSpecialization_ThrowsException() {
            UUID id = UUID.randomUUID();
            Trainer existingTrainer = new Trainer("John", "Smith", "John.Smith", "pass", true, Set.of(strengthTraining));
            existingTrainer.setId(id);

            TrainerUpdateDto dto = new TrainerUpdateDto(id, "John", "Smith", true, Set.of(cardio), null);

            when(trainerRepository.findById(id)).thenReturn(Optional.of(existingTrainer));
            when(trainingTypeRepository.existsById("Cardio")).thenReturn(false);

            assertThrows(EntityDoesNotExistException.class, () -> trainerService.updateTrainer(dto));
            verify(trainerRepository, never()).update(any());
        }

        @Test
        @DisplayName("Should throw TrainingDoesNotBelongToTrainerException when updating linked training belonging to another trainer")
        void updateTrainer_TrainingBelongsToOtherTrainer_ThrowsException() {
            UUID trainerId = UUID.randomUUID();
            UUID otherTrainerId = UUID.randomUUID();
            UUID traineeId = UUID.randomUUID();

            Trainer existingTrainer = new Trainer("John", "Smith", "John.Smith", "pass", true, Set.of(strengthTraining));
            existingTrainer.setId(trainerId);

            TrainingId trainingId = new TrainingId(traineeId, otherTrainerId, LocalDate.now());
            TrainerUpdateDto dto = new TrainerUpdateDto(trainerId, "John", "Smith", true, null, Set.of(trainingId));

            Training mockTraining = new Training();

            when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(existingTrainer));
            when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(mockTraining));

            assertThrows(TrainingDoesNotBelongToTrainerException.class, () -> trainerService.updateTrainer(dto));
            verify(trainerRepository, never()).update(any());
        }
    }
}