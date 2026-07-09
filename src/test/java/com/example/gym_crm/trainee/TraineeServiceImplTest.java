package com.example.gym_crm.trainee;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerRepository;
import com.example.gym_crm.trainer.TrainingDoesNotBelongToTrainerException;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserUtils userUtils;

    private User sampleUser;
    private Trainee sampleTrainee;
    private UUID traineeId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        traineeId = UUID.randomUUID();
        userId = UUID.randomUUID();

        sampleUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .password("password123")
                .isActive(true)
                .build();

        sampleTrainee = Trainee.builder()
                .id(traineeId)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Street")
                .user(sampleUser)
                .trainers(new ArrayList<>())
                .trainings(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("Create Trainee Tests")
    class CreateTraineeTests {

        @Test
        @DisplayName("Should create trainee successfully when inputs are valid")
        void createTrainee_Success() {
            TraineeCreateDto dto = new TraineeCreateDto("John", "Doe", true, LocalDate.of(2000, 1, 1), "123 Street");

            when(userUtils.createUsername("John", "Doe")).thenReturn("John.Doe");
            when(userUtils.generatePassword()).thenReturn("generatedPass");
            when(traineeRepository.save(any(Trainee.class))).thenReturn(sampleTrainee);

            Trainee result = traineeService.createTrainee(dto);

            assertNotNull(result);
            assertEquals("John.Doe", result.getUser().getUsername());
            verify(traineeRepository, times(1)).save(any(Trainee.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when creation DTO is null")
        void createTrainee_NullDto_ThrowsException() {
            assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(null));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when date of birth is in the future")
        void createTrainee_FutureBirthDate_ThrowsException() {
            TraineeCreateDto dto = new TraineeCreateDto("John", "Doe", true, LocalDate.now().plusDays(5), "123 Street");
            assertThrows(IllegalArgumentException.class, () -> traineeService.createTrainee(dto));
        }
    }

    @Nested
    @DisplayName("Update Trainee Tests")
    class UpdateTraineeTests {

        @Test
        @DisplayName("Should update trainee profile details successfully without changing username if names remain identical")
        void updateTrainee_Success_NoNameChange() {
            TraineeUpdateDto dto = new TraineeUpdateDto();
            dto.setUserId(traineeId);
            dto.setFirstName("John");
            dto.setLastName("Doe");
            dto.setIsActive(false);
            dto.setAddress("456 New Ave");

            when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(sampleTrainee));
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);
            when(traineeRepository.save(any(Trainee.class))).thenReturn(sampleTrainee);

            Trainee result = traineeService.updateTrainee(dto);

            assertNotNull(result);
            assertFalse(sampleUser.getIsActive());
            assertEquals("456 New Ave", sampleTrainee.getAddress());
            verify(userUtils, never()).createUsername(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw TrainingDoesNotBelongToTrainerException when a linked training id cross-match check fails")
        void updateTrainee_MismatchedTrainingTrainee_ThrowsException() {
            UUID badTrainingId = UUID.randomUUID();
            TraineeUpdateDto dto = new TraineeUpdateDto();
            dto.setUserId(traineeId);
            dto.setTraining_ids(List.of(badTrainingId));

            Training mismatchedTraining = new Training();
            Trainee otherTrainee = new Trainee();
            otherTrainee.setId(UUID.randomUUID());
            mismatchedTraining.setTrainee(otherTrainee);

            when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(sampleTrainee));
            when(trainingRepository.findById(badTrainingId)).thenReturn(Optional.of(mismatchedTraining));

            assertThrows(TrainingDoesNotBelongToTrainerException.class, () -> traineeService.updateTrainee(dto));
        }
    }

    @Nested
    @DisplayName("Delete Trainee Tests")
    class DeleteTraineeTests {

        @Test
        @DisplayName("Should remove trainee and associated user from database profile references completely")
        void deleteTrainee_Success() {
            when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(sampleTrainee));

            traineeService.deleteTrainee(traineeId);

            verify(traineeRepository, times(1)).deleteById(traineeId);
            verify(userRepository, times(1)).deleteById(userId);
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when attempting to remove a non-existent trainee id")
        void deleteTrainee_NotFound_ThrowsException() {
            when(traineeRepository.findById(traineeId)).thenReturn(Optional.empty());
            assertThrows(EntityDoesNotExistException.class, () -> traineeService.deleteTrainee(traineeId));
        }
    }

    @Nested
    @DisplayName("Read and Auxiliary Management Operations")
    class AuxiliaryOperationsTests {

        @Test
        @DisplayName("Should successfully change target user entity password parameters")
        void changePassword_Success() {
            when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(sampleTrainee));

            Trainee result = traineeService.changePassword("John.Doe", "newSecurePassword");

            assertEquals("newSecurePassword", result.getUser().getPassword());
            verify(userRepository, times(1)).save(sampleUser);
        }

        @Test
        @DisplayName("Should toggle active status flags on update status invocation targets")
        void updateTraineeStatus_Success() {
            when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(sampleTrainee));

            Trainee result = traineeService.updateTraineeStatus("John.Doe");

            assertFalse(result.getUser().getIsActive());
            verify(userRepository, times(1)).save(sampleUser);
        }

        @Test
        @DisplayName("Should correctly delegate criteria search parameters to structural underlying training repositories")
        void getTraineeTrainings_Success() {
            when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(sampleTrainee));
            when(trainingRepository.findTraineeTrainingsByCriteria(eq("John.Doe"), any(), any(), any(), any()))
                    .thenReturn(List.of(new Training()));

            List<Training> results = traineeService.getTraineeTrainings("John.Doe", null, null, null, null);

            assertNotNull(results);
            assertFalse(results.isEmpty());
        }
    }

    @Test
    @DisplayName("Should successfully retrieve trainee profile when querying by a valid ID")
    void getTraineeById_Success() {
        when(traineeRepository.findById(traineeId)).thenReturn(Optional.of(sampleTrainee));

        Trainee result = traineeService.getTraineeById(traineeId);

        assertNotNull(result);
        assertEquals(traineeId, result.getId());
        verify(traineeRepository, times(1)).findById(traineeId);
    }

    @Test
    @DisplayName("Should purge trainee and associated user records completely when matching by username")
    void deleteTraineeByUsername_Success() {
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(sampleTrainee));

        traineeService.deleteTraineeByUsername("John.Doe");

        verify(traineeRepository, times(1)).deleteById(traineeId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should cleanly replace trainee coach tracking associations when given valid trainer list IDs")
    void updateTraineeTrainers_Success() {
        UUID trainerUUID = UUID.randomUUID();
        List<UUID> trainerIds = List.of(trainerUUID);
        Trainer mockTrainer = new Trainer();
        mockTrainer.setId(trainerUUID);

        when(trainerRepository.findById(trainerUUID)).thenReturn(Optional.of(mockTrainer));
        when(traineeRepository.findByUserUsername("John.Doe")).thenReturn(Optional.of(sampleTrainee));
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainee result = traineeService.updateTraineeTrainers("John.Doe", trainerIds);

        assertNotNull(result);
        assertEquals(1, result.getTrainers().size());
        assertEquals(trainerUUID, result.getTrainers().get(0).getId());
        verify(traineeRepository, times(1)).save(sampleTrainee);
    }
}