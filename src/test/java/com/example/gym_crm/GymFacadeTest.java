package com.example.gym_crm.application;

import com.example.gym_crm.authentication.AuthData;
import com.example.gym_crm.trainee.Dto.*;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeServiceFacade;
import com.example.gym_crm.trainer.Dto.*;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerServiceFacade;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Dto.TrainingGetByIdDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingServiceFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    private GymFacade gymFacade;

    @Mock
    private TraineeServiceFacade traineeServiceFacade;

    @Mock
    private TrainerServiceFacade trainerServiceFacade;

    @Mock
    private TrainingServiceFacade trainingServiceFacade;

    @BeforeEach
    void setUp() {
        gymFacade = new GymFacade(traineeServiceFacade, trainerServiceFacade, trainingServiceFacade);
    }

    @Nested
    @DisplayName("Trainee Routing Tests")
    class TraineeRoutingTests {

        @Test
        @DisplayName("Should delegate createTrainee to TraineeServiceFacade")
        void createTrainee_DelegatesCorrectly() {
            TraineeCreateDto dto = mock(TraineeCreateDto.class);
            Trainee expected = new Trainee();
            when(traineeServiceFacade.createTrainee(dto)).thenReturn(expected);

            Trainee result = gymFacade.createTrainee(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeServiceFacade, times(1)).createTrainee(dto);
        }

        @Test
        @DisplayName("Should delegate updateTrainee to TraineeServiceFacade")
        void updateTrainee_DelegatesCorrectly() {
            TraineeUpdateDto dto = mock(TraineeUpdateDto.class);
            Trainee expected = new Trainee();
            when(traineeServiceFacade.updateTrainee(dto)).thenReturn(expected);

            Trainee result = gymFacade.updateTrainee(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeServiceFacade, times(1)).updateTrainee(dto);
        }

        @Test
        @DisplayName("Should delegate deleteTrainee to TraineeServiceFacade")
        void deleteTrainee_DelegatesCorrectly() {
            TraineeDeleteDto dto = mock(TraineeDeleteDto.class);
            doNothing().when(traineeServiceFacade).deleteTrainee(dto);

            gymFacade.deleteTrainee(dto);

            verify(traineeServiceFacade, times(1)).deleteTrainee(dto);
        }

        @Test
        @DisplayName("Should delegate getTraineeById to TraineeServiceFacade")
        void getTraineeById_DelegatesCorrectly() {
            TraineeGetByIdDto dto = mock(TraineeGetByIdDto.class);
            Trainee expected = new Trainee();
            when(traineeServiceFacade.getTraineeById(dto)).thenReturn(expected);

            Trainee result = gymFacade.getTraineeById(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeServiceFacade, times(1)).getTraineeById(dto);
        }

        @Test
        @DisplayName("Should delegate getTraineeByUsername to TraineeServiceFacade")
        void getTraineeByUsername_DelegatesCorrectly() {
            AuthData authData = mock(AuthData.class);
            Trainee expected = new Trainee();
            when(traineeServiceFacade.getTraineeByUsername(authData)).thenReturn(expected);

            Trainee result = gymFacade.getTraineeByUsername(authData);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeServiceFacade, times(1)).getTraineeByUsername(authData);
        }

        @Test
        @DisplayName("Should delegate changeTraineePassword to TraineeServiceFacade")
        void changeTraineePassword_DelegatesCorrectly() {
            TraineeChangePasswordDto dto = mock(TraineeChangePasswordDto.class);
            Trainee expected = new Trainee();
            when(traineeServiceFacade.changePassword(dto)).thenReturn(expected);

            Trainee result = gymFacade.changeTraineePassword(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeServiceFacade, times(1)).changePassword(dto);
        }

        @Test
        @DisplayName("Should delegate updateTraineeStatus to TraineeServiceFacade")
        void updateTraineeStatus_DelegatesCorrectly() {
            AuthData authData = mock(AuthData.class);
            Trainee expected = new Trainee();
            when(traineeServiceFacade.updateTraineeStatus(authData)).thenReturn(expected);

            Trainee result = gymFacade.updateTraineeStatus(authData);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeServiceFacade, times(1)).updateTraineeStatus(authData);
        }

        @Test
        @DisplayName("Should delegate deleteTraineeByUsername to TraineeServiceFacade")
        void deleteTraineeByUsername_DelegatesCorrectly() {
            AuthData authData = mock(AuthData.class);
            doNothing().when(traineeServiceFacade).deleteTraineeByUsername(authData);

            gymFacade.deleteTraineeByUsername(authData);

            verify(traineeServiceFacade, times(1)).deleteTraineeByUsername(authData);
        }

        @Test
        @DisplayName("Should delegate updateTraineeTrainers to TraineeServiceFacade")
        void updateTraineeTrainers_DelegatesCorrectly() {
            TraineeUpdateTrainersDto dto = mock(TraineeUpdateTrainersDto.class);
            Trainee expected = new Trainee();
            when(traineeServiceFacade.updateTraineeTrainers(dto)).thenReturn(expected);

            Trainee result = gymFacade.updateTraineeTrainers(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeServiceFacade, times(1)).updateTraineeTrainers(dto);
        }

        @Test
        @DisplayName("Should delegate getTraineeTrainings to TraineeServiceFacade")
        void getTraineeTrainings_DelegatesCorrectly() {
            TraineeTrainingsSearchDto dto = mock(TraineeTrainingsSearchDto.class);
            List<Training> expected = List.of(new Training());
            when(traineeServiceFacade.getTraineeTrainings(dto)).thenReturn(expected);

            List<Training> result = gymFacade.getTraineeTrainings(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeServiceFacade, times(1)).getTraineeTrainings(dto);
        }
    }

    @Nested
    @DisplayName("Trainer Routing Tests")
    class TrainerRoutingTests {

        @Test
        @DisplayName("Should delegate createTrainer to TrainerServiceFacade")
        void createTrainer_DelegatesCorrectly() {
            TrainerCreateDto dto = mock(TrainerCreateDto.class);
            Trainer expected = new Trainer();
            when(trainerServiceFacade.createTrainer(dto)).thenReturn(expected);

            Trainer result = gymFacade.createTrainer(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerServiceFacade, times(1)).createTrainer(dto);
        }

        @Test
        @DisplayName("Should delegate updateTrainer to TrainerServiceFacade")
        void updateTrainer_DelegatesCorrectly() {
            TrainerUpdateDto dto = mock(TrainerUpdateDto.class);
            Trainer expected = new Trainer();
            when(trainerServiceFacade.updateTrainer(dto)).thenReturn(expected);

            Trainer result = gymFacade.updateTrainer(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerServiceFacade, times(1)).updateTrainer(dto);
        }

        @Test
        @DisplayName("Should delegate getTrainerById to TrainerServiceFacade")
        void getTrainerById_DelegatesCorrectly() {
            TrainerGetByIdDto dto = mock(TrainerGetByIdDto.class);
            Trainer expected = new Trainer();
            when(trainerServiceFacade.getTrainerByID(dto)).thenReturn(expected);

            Trainer result = gymFacade.getTrainerById(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerServiceFacade, times(1)).getTrainerByID(dto);
        }

        @Test
        @DisplayName("Should delegate getTrainerByUsername to TrainerServiceFacade")
        void getTrainerByUsername_DelegatesCorrectly() {
            AuthData authData = mock(AuthData.class);
            Trainer expected = new Trainer();
            when(trainerServiceFacade.getTrainerByUsername(authData)).thenReturn(expected);

            Trainer result = gymFacade.getTrainerByUsername(authData);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerServiceFacade, times(1)).getTrainerByUsername(authData);
        }

        @Test
        @DisplayName("Should delegate changeTrainerPassword to TrainerServiceFacade")
        void changeTrainerPassword_DelegatesCorrectly() {
            TrainerChangePasswordDto dto = mock(TrainerChangePasswordDto.class);
            Trainer expected = new Trainer();
            when(trainerServiceFacade.changePassword(dto)).thenReturn(expected);

            Trainer result = gymFacade.changeTrainerPassword(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerServiceFacade, times(1)).changePassword(dto);
        }

        @Test
        @DisplayName("Should delegate updateTrainerStatus to TrainerServiceFacade")
        void updateTrainerStatus_DelegatesCorrectly() {
            AuthData authData = mock(AuthData.class);
            Trainer expected = new Trainer();
            when(trainerServiceFacade.updateTrainerStatus(authData)).thenReturn(expected);

            Trainer result = gymFacade.updateTrainerStatus(authData);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerServiceFacade, times(1)).updateTrainerStatus(authData);
        }

        @Test
        @DisplayName("Should delegate getTrainerTrainings to TrainerServiceFacade")
        void getTrainerTrainings_DelegatesCorrectly() {
            TrainerTrainingsSearchDto dto = mock(TrainerTrainingsSearchDto.class);
            List<Training> expected = List.of(new Training());
            when(trainerServiceFacade.getTrainerTrainings(dto)).thenReturn(expected);

            List<Training> result = gymFacade.getTrainerTrainings(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerServiceFacade, times(1)).getTrainerTrainings(dto);
        }

        @Test
        @DisplayName("Should delegate getUnassignedTrainers to TrainerServiceFacade")
        void getUnassignedTrainers_DelegatesCorrectly() {
            GetUnassignedTrainersDto dto = mock(GetUnassignedTrainersDto.class);
            List<Trainer> expected = List.of(new Trainer());
            when(trainerServiceFacade.getUnassignedTrainers(dto)).thenReturn(expected);

            List<Trainer> result = gymFacade.getUnassignedTrainers(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerServiceFacade, times(1)).getUnassignedTrainers(dto);
        }
    }

    @Nested
    @DisplayName("Training Routing Tests")
    class TrainingRoutingTests {

        @Test
        @DisplayName("Should delegate createTraining to TrainingServiceFacade")
        void createTraining_DelegatesCorrectly() {
            TrainingCreateDto dto = mock(TrainingCreateDto.class);
            Training expected = new Training();
            when(trainingServiceFacade.addTraining(dto)).thenReturn(expected);

            Training result = gymFacade.createTraining(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainingServiceFacade, times(1)).addTraining(dto);
        }

        @Test
        @DisplayName("Should delegate getTraining to TrainingServiceFacade")
        void getTraining_DelegatesCorrectly() {
            TrainingGetByIdDto dto = mock(TrainingGetByIdDto.class);
            Training expected = new Training();
            when(trainingServiceFacade.getTraining(dto)).thenReturn(expected);

            Training result = gymFacade.getTraining(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainingServiceFacade, times(1)).getTraining(dto);
        }
    }
}