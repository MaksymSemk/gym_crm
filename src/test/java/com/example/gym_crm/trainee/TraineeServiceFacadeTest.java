package com.example.gym_crm.trainee;

import com.example.gym_crm.authentication.AuthData;
import com.example.gym_crm.trainee.Dto.*;
import com.example.gym_crm.training.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceFacadeTest {

    @InjectMocks
    private TraineeServiceFacade traineeServiceFacade;

    @Mock
    private TraineeService traineeService;

    private Trainee expectedTrainee;

    @BeforeEach
    void setUp() {
        expectedTrainee = new Trainee();
    }

    @Test
    @DisplayName("Should delegate createTrainee requirements smoothly to underlying service execution contexts")
    void createTrainee_DelegatesCorrectly() {
        TraineeCreateDto dto = new TraineeCreateDto("Jane", "Smith", true, LocalDate.of(1999, 5, 5), "Address");
        when(traineeService.createTrainee(dto)).thenReturn(expectedTrainee);

        Trainee result = traineeServiceFacade.createTrainee(dto);

        assertNotNull(result);
        assertEquals(expectedTrainee, result);
        verify(traineeService, times(1)).createTrainee(dto);
    }

    @Test
    @DisplayName("Should delegate updateTrainee requirements smoothly to underlying service execution contexts")
    void updateTrainee_DelegatesCorrectly() {
        TraineeUpdateDto dto = mock(TraineeUpdateDto.class);
        when(traineeService.updateTrainee(dto)).thenReturn(expectedTrainee);

        Trainee result = traineeServiceFacade.updateTrainee(dto);

        assertNotNull(result);
        verify(traineeService, times(1)).updateTrainee(dto);
    }

    @Test
    @DisplayName("Should delegate deleteTrainee requirements smoothly to underlying service execution contexts")
    void deleteTrainee_DelegatesCorrectly() {
        TraineeDeleteDto dto = new TraineeDeleteDto();
        UUID id = UUID.randomUUID();
        dto.setId(id);

        doNothing().when(traineeService).deleteTrainee(id);

        traineeServiceFacade.deleteTrainee(dto);

        verify(traineeService, times(1)).deleteTrainee(id);
    }

    @Test
    @DisplayName("Should delegate changePassword credentials changes requirements smoothly to underlying services")
    void changePassword_DelegatesCorrectly() {
        TraineeChangePasswordDto dto = new TraineeChangePasswordDto("user.test", "oldPass", "newPass");
        when(traineeService.changePassword("user.test", "newPass")).thenReturn(expectedTrainee);

        Trainee result = traineeServiceFacade.changePassword(dto);

        assertNotNull(result);
        verify(traineeService, times(1)).changePassword("user.test", "newPass");
    }

    @Test
    @DisplayName("Should delegate search history operations parameters criteria fields filters queries onto services")
    void getTraineeTrainings_DelegatesCorrectly() {
        TraineeTrainingsSearchDto dto = new TraineeTrainingsSearchDto();
        dto.setUsername("user.test");
        dto.setFromDate(LocalDate.now());

        when(traineeService.getTraineeTrainings("user.test", dto.getFromDate(), null, null, null))
                .thenReturn(List.of(new Training()));

        List<Training> trainings = traineeServiceFacade.getTraineeTrainings(dto);

        assertNotNull(trainings);
        verify(traineeService, times(1)).getTraineeTrainings("user.test", dto.getFromDate(), null, null, null);
    }

    @Test
    @DisplayName("Should extract ID from DTO and route to underlying TraineeService lookup logic")
    void getTraineeById_DelegatesCorrectly() {
        TraineeGetByIdDto dto = new TraineeGetByIdDto();
        UUID id = UUID.randomUUID();
        dto.setId(id);

        when(traineeService.getTraineeById(id)).thenReturn(expectedTrainee);

        Trainee result = traineeServiceFacade.getTraineeById(dto);

        assertNotNull(result);
        verify(traineeService, times(1)).getTraineeById(id);
    }

    @Test
    @DisplayName("Should extract username from AuthData wrapper and delegate purge invocation to service layer")
    void deleteTraineeByUsername_DelegatesCorrectly() {
        AuthData authData = new AuthData();
        authData.setUsername("purge.user");
        authData.setPassword("any");

        doNothing().when(traineeService).deleteTraineeByUsername("purge.user");

        traineeServiceFacade.deleteTraineeByUsername(authData);

        verify(traineeService, times(1)).deleteTraineeByUsername("purge.user");
    }

    @Test
    @DisplayName("Should map trainer list payload structures over to structural service re-assignment engines")
    void updateTraineeTrainers_DelegatesCorrectly() {
        TraineeUpdateTrainersDto dto = new TraineeUpdateTrainersDto();
        dto.setUsername("trainee.user");
        UUID trainerId = UUID.randomUUID();
        dto.setTrainerIds(List.of(trainerId));

        when(traineeService.updateTraineeTrainers("trainee.user", List.of(trainerId))).thenReturn(expectedTrainee);

        Trainee result = traineeServiceFacade.updateTraineeTrainers(dto);

        assertNotNull(result);
        verify(traineeService, times(1)).updateTraineeTrainers("trainee.user", List.of(trainerId));
    }
}