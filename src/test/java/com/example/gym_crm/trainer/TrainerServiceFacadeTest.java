package com.example.gym_crm.trainer;

import com.example.gym_crm.authentication.AuthData;
import com.example.gym_crm.trainer.Dto.*;
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
class TrainerServiceFacadeTest {

    @InjectMocks
    private TrainerServiceFacade trainerServiceFacade;

    @Mock
    private TrainerService trainerService;

    private Trainer expectedTrainer;

    @BeforeEach
    void setUp() {
        expectedTrainer = new Trainer();
    }

    @Test
    @DisplayName("Should extract contract fields and route payload seamlessly over to creation services")
    void createTrainer_DelegatesCorrectly() {
        TrainerCreateDto dto = new TrainerCreateDto("Alex", "Turner", true, 1L);
        when(trainerService.createTrainer(dto)).thenReturn(expectedTrainer);

        Trainer result = trainerServiceFacade.createTrainer(dto);

        assertNotNull(result);
        verify(trainerService, times(1)).createTrainer(dto);
    }

    @Test
    @DisplayName("Should pull identity names from authorization contexts and fetch data profiles")
    void getTrainerByUsername_DelegatesCorrectly() {
        AuthData authData = new AuthData();
        authData.setUsername("trainer.test");

        when(trainerService.getTrainerByUsername("trainer.test")).thenReturn(expectedTrainer);

        Trainer result = trainerServiceFacade.getTrainerByUsername(authData);

        assertNotNull(result);
        verify(trainerService, times(1)).getTrainerByUsername("trainer.test");
    }

    @Test
    @DisplayName("Should map modification object representations seamlessly down into internal update routines")
    void updateTrainer_DelegatesCorrectly() {
        TrainerUpdateDto dto = mock(TrainerUpdateDto.class);
        when(trainerService.updateTrainer(dto)).thenReturn(expectedTrainer);

        Trainer result = trainerServiceFacade.updateTrainer(dto);

        assertNotNull(result);
        verify(trainerService, times(1)).updateTrainer(dto);
    }

    @Test
    @DisplayName("Should unwrap password update parameters and execute backend modification steps")
    void changePassword_DelegatesCorrectly() {
        TrainerChangePasswordDto dto = mock(TrainerChangePasswordDto.class);
        when(dto.getUsername()).thenReturn("trainer.test");
        when(dto.getNewPassword()).thenReturn("brandNewPass");
        when(trainerService.changePassword("trainer.test", "brandNewPass")).thenReturn(expectedTrainer);

        Trainer result = trainerServiceFacade.changePassword(dto);

        assertNotNull(result);
        verify(trainerService, times(1)).changePassword("trainer.test", "brandNewPass");
    }

    @Test
    @DisplayName("Should extract wrapper username parameters and forward to status toggle engines")
    void updateTrainerStatus_DelegatesCorrectly() {
        AuthData authData = new AuthData();
        authData.setUsername("trainer.test");
        when(trainerService.updateTrainerStatus("trainer.test")).thenReturn(expectedTrainer);

        Trainer result = trainerServiceFacade.updateTrainerStatus(authData);

        assertNotNull(result);
        verify(trainerService, times(1)).updateTrainerStatus("trainer.test");
    }

    @Test
    @DisplayName("Should flatten history lookup DTO parameters into structured search filter calls")
    void getTrainerTrainings_DelegatesCorrectly() {
        TrainerTrainingsSearchDto dto = new TrainerTrainingsSearchDto();
        dto.setUsername("trainer.test");
        dto.setFromDate(LocalDate.now());
        dto.setTraineeName("trainee.test");

        when(trainerService.getTrainerTrainings("trainer.test", dto.getFromDate(), null, "trainee.test"))
                .thenReturn(List.of(new Training()));

        List<Training> results = trainerServiceFacade.getTrainerTrainings(dto);

        assertNotNull(results);
        verify(trainerService, times(1)).getTrainerTrainings("trainer.test", dto.getFromDate(), null, "trainee.test");
    }

    @Test
    @DisplayName("Should uncurry isolation queries for retrieving coaches who are unassigned to specific trainees")
    void getUnassignedTrainers_DelegatesCorrectly() {
        GetUnassignedTrainersDto dto = new GetUnassignedTrainersDto();
        dto.setTraineeUsername("trainee.test");

        when(trainerService.getUnassignedTrainersByTraineeUsername("trainee.test")).thenReturn(List.of(expectedTrainer));

        List<Trainer> results = trainerServiceFacade.getUnassignedTrainers(dto);

        assertNotNull(results);
        verify(trainerService, times(1)).getUnassignedTrainersByTraineeUsername("trainee.test");
    }

    @Test
    @DisplayName("Should pass profile validation primary identifiers accurately down into identity retrieval layers")
    void getTrainerByID_DelegatesCorrectly() {
        TrainerGetByIdDto dto = new TrainerGetByIdDto();
        UUID searchId = UUID.randomUUID();
        dto.setId(searchId);

        when(trainerService.getTrainerByID(searchId)).thenReturn(expectedTrainer);

        Trainer result = trainerServiceFacade.getTrainerByID(dto);

        assertNotNull(result);
        verify(trainerService, times(1)).getTrainerByID(searchId);
    }
}