package com.example.gym_crm.services;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeRepository;
import com.example.gym_crm.trainee.TraineeServiceImpl;
import com.example.gym_crm.trainee.TrainingDoesNotBelongToTraineeException;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingRepository;
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
class TraineeServiceTest {

    private TraineeServiceImpl traineeService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private UserUtils userUtils;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl();
        traineeService.setTraineeRepository(traineeRepository);
        traineeService.setTrainingRepository(trainingRepository);
        traineeService.setUserUtils(userUtils);
    }

    @Nested
    @DisplayName("Create Trainee Tests")
    class CreateTraineeTests {

        @Test
        @DisplayName("Should create trainee successfully when input is valid")
        void createTrainee_Success() {
            TraineeCreateDto dto = new TraineeCreateDto("John", "Doe", true, LocalDate.of(2000, 1, 1), "123 St");
            UUID generatedId = UUID.randomUUID();

            when(userUtils.createUsername("John", "Doe")).thenReturn("John.Doe");
            when(userUtils.generatePassword()).thenReturn("pass123456");

            when(traineeRepository.create(any(Trainee.class))).thenAnswer(invocation -> {
                Trainee trainee = invocation.getArgument(0);
                trainee.setId(generatedId);
                return trainee;
            });

            Trainee result = traineeService.createTrainee(dto);

            assertNotNull(result);
            assertEquals(generatedId, result.getId());
            assertEquals("John.Doe", result.getUsername());
            assertEquals("pass123456", result.getPassword());
            verify(traineeRepository, times(1)).create(any(Trainee.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when DTO is null")
        void createTrainee_NullDto_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(null));
            verifyNoInteractions(traineeRepository, userUtils);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when date of birth is in the future")
        void createTrainee_FutureBirthDate_ThrowsException() {
            TraineeCreateDto dto = new TraineeCreateDto("John", "Doe", true, LocalDate.now().plusDays(1), "123 St");

            assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(dto));
            verifyNoInteractions(traineeRepository, userUtils);
        }
    }

    @Nested
    @DisplayName("Update Trainee Tests")
    class UpdateTraineeTests {

        @Test
        @DisplayName("Should update trainee successfully without renaming identity")
        void updateTrainee_Success_NoIdentityChange() {
            UUID traineeId = UUID.randomUUID();
            Trainee existingTrainee = new Trainee("John", "Doe", "John.Doe", "oldPass", true, LocalDate.of(2000, 1, 1), "123 St", traineeId);
            TraineeUpdateDto dto = new TraineeUpdateDto(traineeId, "John", "Doe", false, LocalDate.of(2000, 1, 1), "456 New St", null);

            when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
            when(traineeRepository.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

            Trainee result = traineeService.updateTrainee(dto);

            assertNotNull(result);
            assertFalse(result.getIsActive());
            assertEquals("456 New St", result.getAddress());
            assertEquals("John.Doe", result.getUsername()); // Username unchanged because identity didn't change
            verify(userUtils, never()).createUsername(anyString(), anyString());
        }

        @Test
        @DisplayName("Should update trainee and recompute username when identity name changes")
        void updateTrainee_Success_WithIdentityChange() {
            UUID traineeId = UUID.randomUUID();
            Trainee existingTrainee = new Trainee("John", "Doe", "John.Doe", "pass", true, LocalDate.of(2000, 1, 1), "123 St", traineeId);
            TraineeUpdateDto dto = new TraineeUpdateDto(traineeId, "Johnny", "Smith", true, LocalDate.of(2000, 1, 1), "123 St", null);

            when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
            when(userUtils.createUsername("Johnny", "Smith")).thenReturn("Johnny.Smith");
            when(traineeRepository.update(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

            Trainee result = traineeService.updateTrainee(dto);

            assertEquals("Johnny.Smith", result.getUsername());
            verify(userUtils, times(1)).createUsername("Johnny", "Smith");
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when trainee is missing")
        void updateTrainee_TraineeNotFound_ThrowsException() {
            UUID traineeId = UUID.randomUUID();
            TraineeUpdateDto dto = new TraineeUpdateDto(traineeId, "John", "Doe", true, LocalDate.now(), "St", null);

            when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());

            assertThrows(EntityDoesNotExistException.class, () -> traineeService.updateTrainee(dto));
            verify(traineeRepository, never()).update(any());
        }

        @Test
        @DisplayName("Should throw TrainingDoesNotBelongToTraineeException when training cross-match check fails")
        void updateTrainee_TrainingBelongsToOtherTrainee_ThrowsException() {
            UUID traineeId = UUID.randomUUID();
            UUID otherTraineeId = UUID.randomUUID();
            UUID trainerId = UUID.randomUUID();

            Trainee existingTrainee = new Trainee("John", "Doe", "John.Doe", "pass", true, LocalDate.of(2000, 1, 1), "123 St", traineeId);
            TrainingId trainingId = new TrainingId(otherTraineeId, trainerId, LocalDate.now()); // Mismatched trainee ID inside composite key
            TraineeUpdateDto dto = new TraineeUpdateDto(traineeId, "John", "Doe", true, LocalDate.of(2000, 1, 1), "123 St", Set.of(trainingId));

            Training mockTraining = new Training();

            when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(existingTrainee));
            when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(mockTraining));

            assertThrows(TrainingDoesNotBelongToTraineeException.class, () -> traineeService.updateTrainee(dto));
            verify(traineeRepository, never()).update(any());
        }
    }

    @Nested
    @DisplayName("Delete Trainee Tests")
    class DeleteTraineeTests {

        @Test
        @DisplayName("Should pass ID to repository when execution is successful")
        void deleteTrainee_Success() {
            UUID id = UUID.randomUUID();

            traineeService.deleteTrainee(id);

            verify(traineeRepository, times(1)).delete(id);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when ID is null")
        void deleteTrainee_NullId_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> traineeService.deleteTrainee(null));
        }
    }

    @Nested
    @DisplayName("Select Trainee Tests")
    class SelectTraineeTests {

        @Test
        @DisplayName("Should return trainee profile when requested ID exists")
        void getTraineeById_Success() {
            UUID id = UUID.randomUUID();
            Trainee expected = new Trainee();
            expected.setId(id);

            when(traineeRepository.findById(id)).thenReturn(Optional.of(expected));

            Trainee result = traineeService.getTraineeById(id);

            assertNotNull(result);
            assertEquals(id, result.getId());
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when ID does not match any stored records")
        void getTraineeById_NotFound_ThrowsException() {
            UUID id = UUID.randomUUID();
            when(traineeRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(EntityDoesNotExistException.class, () -> traineeService.getTraineeById(id));
        }
    }
}