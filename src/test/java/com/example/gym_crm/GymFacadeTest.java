package com.example.gym_crm.application;

import com.example.gym_crm.authentication.AuthData;
import com.example.gym_crm.trainee.Dto.*;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeService;
import com.example.gym_crm.trainer.Dto.*;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerService;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Dto.TrainingGetByIdDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    private GymFacade gymFacade;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        gymFacade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Nested
    @DisplayName("Trainee Routing Tests")
    class TraineeRoutingTests {

        @Test
        @DisplayName("Should delegate createTrainee to TraineeService")
        void createTrainee_DelegatesCorrectly() {
            TraineeCreateDto dto = mock(TraineeCreateDto.class);
            Trainee expected = new Trainee();
            when(traineeService.createTrainee(dto)).thenReturn(expected);

            Trainee result = gymFacade.createTrainee(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).createTrainee(dto);
        }

        @Test
        @DisplayName("Should delegate updateTrainee to TraineeService")
        void updateTrainee_DelegatesCorrectly() {
            TraineeUpdateDto dto = mock(TraineeUpdateDto.class);
            Trainee expected = new Trainee();
            when(traineeService.updateTrainee(dto)).thenReturn(expected);

            Trainee result = gymFacade.updateTrainee(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).updateTrainee(dto);
        }

        @Test
        @DisplayName("Should delegate deleteTrainee ID parameter extraction to TraineeService")
        void deleteTrainee_DelegatesCorrectly() {
            TraineeDeleteDto dto = mock(TraineeDeleteDto.class);
            UUID id = UUID.randomUUID();
            when(dto.getId()).thenReturn(id);
            doNothing().when(traineeService).deleteTrainee(id);

            gymFacade.deleteTrainee(dto);

            verify(traineeService, times(1)).deleteTrainee(id);
        }

        @Test
        @DisplayName("Should delegate getTraineeById ID parameter extraction to TraineeService")
        void getTraineeById_DelegatesCorrectly() {
            TraineeGetByIdDto dto = mock(TraineeGetByIdDto.class);
            UUID id = UUID.randomUUID();
            when(dto.getId()).thenReturn(id);
            Trainee expected = new Trainee();
            when(traineeService.getTraineeById(id)).thenReturn(expected);

            Trainee result = gymFacade.getTraineeById(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).getTraineeById(id);
        }

        @Test
        @DisplayName("Should delegate getTraineeByUsername string parameter extraction to TraineeService")
        void getTraineeByUsername_DelegatesCorrectly() {
            AuthData authData = mock(AuthData.class);
            when(authData.getUsername()).thenReturn("john.doe");
            Trainee expected = new Trainee();
            when(traineeService.getTraineeByUsername("john.doe")).thenReturn(expected);

            Trainee result = gymFacade.getTraineeByUsername(authData);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).getTraineeByUsername("john.doe");
        }

        @Test
        @DisplayName("Should delegate changeTraineePassword DTO payload directly to TraineeService")
        void changeTraineePassword_DelegatesCorrectly() {
            TraineeChangePasswordDto dto = mock(TraineeChangePasswordDto.class);
            Trainee expected = new Trainee();
            when(traineeService.changePassword(dto)).thenReturn(expected);

            Trainee result = gymFacade.changeTraineePassword(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).changePassword(dto);
        }

        @Test
        @DisplayName("Should delegate updateTraineeStatus username verification parameter directly to TraineeService")
        void updateTraineeStatus_DelegatesCorrectly() {
            var status= false;
            AuthData authData = mock(AuthData.class);
            when(authData.getUsername()).thenReturn("john.doe");
            Trainee expected = new Trainee();
            when(traineeService.updateTraineeStatus("john.doe", status)).thenReturn(expected);

            Trainee result = gymFacade.updateTraineeStatus(authData, status);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).updateTraineeStatus("john.doe", status);
        }

        @Test
        @DisplayName("Should delegate deleteTraineeByUsername string conversion invocation to TraineeService")
        void deleteTraineeByUsername_DelegatesCorrectly() {
            AuthData authData = mock(AuthData.class);
            when(authData.getUsername()).thenReturn("john.doe");
            doNothing().when(traineeService).deleteTraineeByUsername("john.doe");

            gymFacade.deleteTraineeByUsername(authData);

            verify(traineeService, times(1)).deleteTraineeByUsername("john.doe");
        }

        @Test
        @DisplayName("Should delegate updateTraineeTrainers assignment DTO directly to TraineeService")
        void updateTraineeTrainers_DelegatesCorrectly() {
            TraineeUpdateTrainersDto dto = mock(TraineeUpdateTrainersDto.class);
            Trainee expected = new Trainee();
            when(traineeService.updateTraineeTrainers(dto)).thenReturn(expected);

            Trainee result = gymFacade.updateTraineeTrainers(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).updateTraineeTrainers(dto);
        }

        @Test
        @DisplayName("Should delegate getTraineeTrainings criteria filter search payload directly to TraineeService")
        void getTraineeTrainings_DelegatesCorrectly() {
            TraineeTrainingsSearchDto dto = mock(TraineeTrainingsSearchDto.class);
            List<Training> expected = List.of(new Training());
            when(traineeService.getTraineeTrainings(dto)).thenReturn(expected);

            List<Training> result = gymFacade.getTraineeTrainings(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).getTraineeTrainings(dto);
        }
    }

    @Nested
    @DisplayName("Trainer Routing Tests")
    class TrainerRoutingTests {

        @Test
        @DisplayName("Should delegate createTrainer setup payload variables to TrainerService")
        void createTrainer_DelegatesCorrectly() {
            TrainerCreateDto dto = mock(TrainerCreateDto.class);
            Trainer expected = new Trainer();
            when(trainerService.createTrainer(dto)).thenReturn(expected);

            Trainer result = gymFacade.createTrainer(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).createTrainer(dto);
        }

        @Test
        @DisplayName("Should delegate updateTrainer verification structures to TrainerService")
        void updateTrainer_DelegatesCorrectly() {
            TrainerUpdateDto dto = mock(TrainerUpdateDto.class);
            Trainer expected = new Trainer();
            when(trainerService.updateTrainer(dto)).thenReturn(expected);

            Trainer result = gymFacade.updateTrainer(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).updateTrainer(dto);
        }

        @Test
        @DisplayName("Should delegate getTrainerById extraction key variable to TrainerService")
        void getTrainerById_DelegatesCorrectly() {
            TrainerGetByIdDto dto = mock(TrainerGetByIdDto.class);
            UUID id = UUID.randomUUID();
            when(dto.getId()).thenReturn(id);
            Trainer expected = new Trainer();
            when(trainerService.getTrainerByID(id)).thenReturn(expected);

            Trainer result = gymFacade.getTrainerById(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).getTrainerByID(id);
        }

        @Test
        @DisplayName("Should delegate getTrainerByUsername string transformation parameters to TrainerService")
        void getTrainerByUsername_DelegatesCorrectly() {
            AuthData authData = mock(AuthData.class);
            when(authData.getUsername()).thenReturn("trainer.smith");
            Trainer expected = new Trainer();
            when(trainerService.getTrainerByUsername("trainer.smith")).thenReturn(expected);

            Trainer result = gymFacade.getTrainerByUsername(authData);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).getTrainerByUsername("trainer.smith");
        }

        @Test
        @DisplayName("Should delegate changeTrainerPassword parameters tracking directly onto TrainerService")
        void changeTrainerPassword_DelegatesCorrectly() {
            TrainerChangePasswordDto dto = mock(TrainerChangePasswordDto.class);
            Trainer expected = new Trainer();
            when(trainerService.changePassword(dto)).thenReturn(expected);

            Trainer result = gymFacade.changeTrainerPassword(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).changePassword(dto);
        }

        @Test
        @DisplayName("Should delegate updateTrainerStatus string lookup value parameters down to TrainerService")
        void updateTrainerStatus_DelegatesCorrectly() {
            var status = false;
            AuthData authData = mock(AuthData.class);
            when(authData.getUsername()).thenReturn("trainer.smith");
            Trainer expected = new Trainer();
            when(trainerService.updateTrainerStatus("trainer.smith", status)).thenReturn(expected);

            Trainer result = gymFacade.updateTrainerStatus(authData, status);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).updateTrainerStatus("trainer.smith",  status);
        }

        @Test
        @DisplayName("Should delegate getTrainerTrainings criteria lookup filtering data down to TrainerService")
        void getTrainerTrainings_DelegatesCorrectly() {
            TrainerTrainingsSearchDto dto = mock(TrainerTrainingsSearchDto.class);
            List<Training> expected = List.of(new Training());
            when(trainerService.getTrainerTrainings(dto)).thenReturn(expected);

            List<Training> result = gymFacade.getTrainerTrainings(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).getTrainerTrainings(dto);
        }

        @Test
        @DisplayName("Should delegate getUnassignedTrainers username context querying filters down to TrainerService")
        void getUnassignedTrainers_DelegatesCorrectly() {
            GetUnassignedTrainersDto dto = mock(GetUnassignedTrainersDto.class);
            when(dto.getTraineeUsername()).thenReturn("trainee.john");
            List<Trainer> expected = List.of(new Trainer());
            when(trainerService.getUnassignedTrainersByTraineeUsername("trainee.john")).thenReturn(expected);

            List<Trainer> result = gymFacade.getUnassignedTrainers(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).getUnassignedTrainersByTraineeUsername("trainee.john");
        }
    }

    @Nested
    @DisplayName("Training Routing Tests")
    class TrainingRoutingTests {

        @Test
        @DisplayName("Should delegate createTraining structural logic validation parameter targets onto TrainingService")
        void createTraining_DelegatesCorrectly() {
            TrainingCreateDto dto = mock(TrainingCreateDto.class);
            Training expected = new Training();
            when(trainingService.createTraining(dto)).thenReturn(expected);

            Training result = gymFacade.createTraining(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainingService, times(1)).createTraining(dto);
        }

        @Test
        @DisplayName("Should delegate getTraining matching verification key elements down into TrainingService")
        void getTraining_DelegatesCorrectly() {
            TrainingGetByIdDto dto = mock(TrainingGetByIdDto.class);
            UUID id = UUID.randomUUID();
            when(dto.getId()).thenReturn(id);
            Training expected = new Training();
            when(trainingService.getTraining(id)).thenReturn(expected);

            Training result = gymFacade.getTraining(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainingService, times(1)).getTraining(id);
        }
    }
}