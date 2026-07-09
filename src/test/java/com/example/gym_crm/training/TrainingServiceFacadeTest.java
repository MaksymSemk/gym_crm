package com.example.gym_crm.training;

import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Dto.TrainingGetByIdDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceFacadeTest {

    @InjectMocks
    private TrainingServiceFacade trainingServiceFacade;

    @Mock
    private TrainingService trainingService;

    private Training expectedTraining;

    @BeforeEach
    void setUp() {
        expectedTraining = new Training();
    }

    @Test
    @DisplayName("Should validate and map metrics down to core creation operations")
    void addTraining_DelegatesCorrectly() {
        TrainingCreateDto dto = mock(TrainingCreateDto.class);
        when(trainingService.createTraining(dto)).thenReturn(expectedTraining);

        Training result = trainingServiceFacade.addTraining(dto);

        assertNotNull(result);
        assertEquals(expectedTraining, result);
        verify(trainingService, times(1)).createTraining(dto);
    }

    @Test
    @DisplayName("Should extract primitive identifiers from lookup request DTO arguments cleanly")
    void getTraining_DelegatesCorrectly() {
        TrainingGetByIdDto dto = new TrainingGetByIdDto();
        UUID trainingUUID = UUID.randomUUID();
        dto.setId(trainingUUID);

        when(trainingService.getTraining(trainingUUID)).thenReturn(expectedTraining);

        Training result = trainingServiceFacade.getTraining(dto);

        assertNotNull(result);
        verify(trainingService, times(1)).getTraining(trainingUUID);
    }
}