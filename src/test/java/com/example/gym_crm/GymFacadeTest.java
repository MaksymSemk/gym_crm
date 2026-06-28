package com.example.gym_crm;

import com.example.gym_crm.application.GymFacade;
import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeService;
import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerService;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    @DisplayName("Trainee Facade Routing Tests")
    class TraineeFacadeTests {

        @Test
        @DisplayName("Should cleanly delegate trainee creation data to TraineeService")
        void createTrainee_DelegatesToService() {
            TraineeCreateDto dto = mock(TraineeCreateDto.class);
            Trainee expected = new Trainee();
            when(traineeService.createTrainee(dto)).thenReturn(expected);

            Trainee result = gymFacade.createTrainee(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).createTrainee(dto);
        }

        @Test
        @DisplayName("Should cleanly delegate trainee update payload data to TraineeService")
        void updateTrainee_DelegatesToService() {
            TraineeUpdateDto dto = mock(TraineeUpdateDto.class);
            Trainee expected = new Trainee();
            when(traineeService.updateTrainee(dto)).thenReturn(expected);

            Trainee result = gymFacade.updateTrainee(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).updateTrainee(dto);
        }

        @Test
        @DisplayName("Should route trainee deletion tracking ID to TraineeService")
        void deleteTrainee_DelegatesToService() {
            UUID id = UUID.randomUUID();
            doNothing().when(traineeService).deleteTrainee(id);

            gymFacade.deleteTrainee(id);

            verify(traineeService, times(1)).deleteTrainee(id);
        }

        @Test
        @DisplayName("Should forward search filter ID key query to TraineeService")
        void getTraineeById_DelegatesToService() {
            UUID id = UUID.randomUUID();
            Trainee expected = new Trainee();
            when(traineeService.getTraineeById(id)).thenReturn(expected);

            Trainee result = gymFacade.getTraineeById(id);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(traineeService, times(1)).getTraineeById(id);
        }
    }

    @Nested
    @DisplayName("Trainer Facade Routing Tests")
    class TrainerFacadeTests {

        @Test
        @DisplayName("Should cleanly delegate trainer creation data to TrainerService")
        void createTrainer_DelegatesToService() {
            TrainerCreateDto dto = mock(TrainerCreateDto.class);
            Trainer expected = new Trainer();
            when(trainerService.createTrainer(dto)).thenReturn(expected);

            Trainer result = gymFacade.createTrainer(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).createTrainer(dto);
        }

        @Test
        @DisplayName("Should cleanly delegate trainer update payload data to TrainerService")
        void updateTrainer_DelegatesToService() {
            TrainerUpdateDto dto = mock(TrainerUpdateDto.class);
            Trainer expected = new Trainer();
            when(trainerService.updateTrainer(dto)).thenReturn(expected);

            Trainer result = gymFacade.updateTrainer(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).updateTrainer(dto);
        }

        @Test
        @DisplayName("Should forward unique identification lookups to TrainerService")
        void getTrainerById_DelegatesToService() {
            UUID id = UUID.randomUUID();
            Trainer expected = new Trainer();
            when(trainerService.getTrainerByID(id)).thenReturn(expected);

            Trainer result = gymFacade.getTrainerById(id);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainerService, times(1)).getTrainerByID(id);
        }
    }

    @Nested
    @DisplayName("Training Facade Routing Tests")
    class TrainingFacadeTests {

        @Test
        @DisplayName("Should cleanly delegate training allocation metrics to TrainingService")
        void createTraining_DelegatesToService() {
            TrainingCreateDto dto = mock(TrainingCreateDto.class);
            Training expected = new Training();
            when(trainingService.createTraining(dto)).thenReturn(expected);

            Training result = gymFacade.createTraining(dto);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainingService, times(1)).createTraining(dto);
        }

        @Test
        @DisplayName("Should route nested composite key parameter targets into TrainingService query engines")
        void getTraining_DelegatesToService() {
            TrainingId id = new TrainingId(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now());
            Training expected = new Training();
            when(trainingService.getTraining(id)).thenReturn(expected);

            Training result = gymFacade.getTraining(id);

            assertNotNull(result);
            assertEquals(expected, result);
            verify(trainingService, times(1)).getTraining(id);
        }
    }
}