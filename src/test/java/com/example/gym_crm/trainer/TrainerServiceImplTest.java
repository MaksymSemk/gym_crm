package com.example.gym_crm.trainer;

import com.example.gym_crm.common.exception.EntityDoesNotExistException;
import com.example.gym_crm.common.user.User;
import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.common.user.UserUtils;
import com.example.gym_crm.trainer.Dto.TrainerChangePasswordDto;
import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerTrainingsSearchDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.repository.TrainingRepository;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
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
class TrainerServiceImplTest {

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private UserUtils userUtils;

    private Trainer sampleTrainer;
    private User sampleUser;
    private TrainingType sampleSpecialization;
    private UUID trainerId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        trainerId = UUID.randomUUID();
        userId = UUID.randomUUID();

        sampleSpecialization = new TrainingType();
        sampleSpecialization.setId(1L);
        sampleSpecialization.setName("Fitness");

        sampleUser = User.builder()
                .id(userId)
                .firstName("Alex")
                .lastName("Turner")
                .username("Alex.Turner")
                .password("trainerPass")
                .isActive(true)
                .build();

        sampleTrainer = Trainer.builder()
                .id(trainerId)
                .specialization(sampleSpecialization)
                .user(sampleUser)
                .trainings(new ArrayList<>())
                .trainees(new ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("Create Trainer Tests")
    class CreateTrainerTests {

        @Test
        @DisplayName("Should save trainer successfully when specialization exists")
        void createTrainer_Success() {
            TrainerCreateDto dto = new TrainerCreateDto("Alex", "Turner", true, 1L);

            when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(sampleSpecialization));
            when(userUtils.createUsername("Alex", "Turner")).thenReturn("Alex.Turner");
            when(userUtils.generatePassword()).thenReturn("pass123");
            when(trainerRepository.save(any(Trainer.class))).thenReturn(sampleTrainer);

            Trainer result = trainerService.createTrainer(dto);

            assertNotNull(result);
            assertEquals("Alex.Turner", result.getUser().getUsername());
            verify(trainerRepository, times(1)).save(any(Trainer.class));
        }

        @Test
        @DisplayName("Should throw EntityDoesNotExistException when specialization ID cannot be resolved")
        void createTrainer_InvalidSpecialization_ThrowsException() {
            TrainerCreateDto dto = new TrainerCreateDto("Alex", "Turner", true, 99L);
            when(trainingTypeRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(EntityDoesNotExistException.class, () -> trainerService.createTrainer(dto));
        }
    }

    @Nested
    @DisplayName("Read and Query Tests")
    class ReadTests {

        @Test
        @DisplayName("Should find trainer when looking up by an existing username string")
        void getTrainerByUsername_Success() {
            when(trainerRepository.findByUserUsername("Alex.Turner")).thenReturn(Optional.of(sampleTrainer));

            Trainer result = trainerService.getTrainerByUsername("Alex.Turner");

            assertNotNull(result);
            assertEquals(trainerId, result.getId());
        }

        @Test
        @DisplayName("Should find trainer when looking up by raw UUID")
        void getTrainerByID_Success() {
            when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(sampleTrainer));

            Trainer result = trainerService.getTrainerByID(trainerId);

            assertNotNull(result);
            assertEquals("Alex.Turner", result.getUser().getUsername());
        }

        @Test
        @DisplayName("Should fetch unassigned trainers using sub-query criteria on the repository layer")
        void getUnassignedTrainersByTraineeUsername_Success() {
            when(trainerRepository.findTrainersNotAssignedToTrainee("trainee.user")).thenReturn(List.of(sampleTrainer));

            List<Trainer> results = trainerService.getUnassignedTrainersByTraineeUsername("trainee.user");

            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
        }
    }

    @Nested
    @DisplayName("Update and Modification Tests")
    class UpdateTests {

        @Test
        @DisplayName("Should modify trainer specialization and identity status correctly")
        void updateTrainer_Success() {
            TrainerUpdateDto dto = new TrainerUpdateDto();
            dto.setUserId(trainerId);
            dto.setFirstName("Alex");
            dto.setLastName("Turner");
            dto.setIsActive(false);
            dto.setSpecializationId(2L);

            TrainingType targetSpecialization = new TrainingType();
            targetSpecialization.setId(2L);

            when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(sampleTrainer));
            when(trainingTypeRepository.findById(2L)).thenReturn(Optional.of(targetSpecialization));
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);
            when(trainerRepository.save(any(Trainer.class))).thenReturn(sampleTrainer);

            Trainer result = trainerService.updateTrainer(dto);

            assertNotNull(result);
            assertFalse(sampleUser.getIsActive());
            assertEquals(targetSpecialization, sampleTrainer.getSpecialization());
        }

        @Test
        @DisplayName("Should modify password field variables safely inside user context objects via DTO payload parameters")
        void changePassword_Success() {
            TrainerChangePasswordDto dto = new TrainerChangePasswordDto();
            dto.setUsername("Alex.Turner");
            dto.setPassword("trainerPass");
            dto.setNewPassword("newSecurePass");

            when(trainerRepository.findByUserUsername("Alex.Turner")).thenReturn(Optional.of(sampleTrainer));
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            Trainer result = trainerService.changePassword(dto);

            assertEquals("newSecurePass", result.getUser().getPassword());
            verify(userRepository, times(1)).save(sampleUser);
        }

        @Test
        @DisplayName("Should toggle current state of activation flags when calling status update engines")
        void updateTrainerStatus_Success() {
            when(trainerRepository.findByUserUsername("Alex.Turner")).thenReturn(Optional.of(sampleTrainer));
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);

            Trainer result = trainerService.updateTrainerStatus("Alex.Turner");

            assertFalse(result.getUser().getIsActive());
        }
    }

    @Nested
    @DisplayName("Filtering and Search Criteria Tests")
    class SearchTests {

        @Test
        @DisplayName("Should delegate filter criteria DTO arguments securely over to core training search engines")
        void getTrainerTrainings_Success() {
            LocalDate now = LocalDate.now();
            TrainerTrainingsSearchDto dto = new TrainerTrainingsSearchDto();
            dto.setUsername("Alex.Turner");
            dto.setFromDate(now);
            dto.setToDate(now);
            dto.setTraineeName("John.Doe");

            when(trainerRepository.findByUserUsername("Alex.Turner")).thenReturn(Optional.of(sampleTrainer));
            when(trainingRepository.findTrainerTrainingsByCriteria("Alex.Turner", now, now, "John.Doe"))
                    .thenReturn(List.of(new Training()));

            List<Training> results = trainerService.getTrainerTrainings(dto);

            assertNotNull(results);
            assertEquals(1, results.size());
            verify(trainingRepository, times(1))
                    .findTrainerTrainingsByCriteria("Alex.Turner", now, now, "John.Doe");
        }
    }
}