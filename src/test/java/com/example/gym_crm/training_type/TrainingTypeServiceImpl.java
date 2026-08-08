package com.example.gym_crm.training_type;

import com.example.gym_crm.training_type.Dto.response.TrainingTypeResponse;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Test
    @DisplayName("Should return mapped TrainingTypeResponse list when entities exist")
    void getAllTrainingTypes_ReturnsMappedList() {
        TrainingType fitness = new TrainingType();
        fitness.setId(1L);
        fitness.setName("Fitness");

        TrainingType yoga = new TrainingType();
        yoga.setId(2L);
        yoga.setName("Yoga");

        when(trainingTypeRepository.findAll()).thenReturn(List.of(fitness, yoga));

        List<TrainingTypeResponse> result = trainingTypeService.getAllTrainingTypes();

        assertThat(result)
                .hasSize(2)
                .extracting(TrainingTypeResponse::trainingType)
                .containsExactly("Fitness", "Yoga");

        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(1).id()).isEqualTo(2L);

        verify(trainingTypeRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no training types exist")
    void getAllTrainingTypes_EmptyRepository_ReturnsEmptyList() {
        when(trainingTypeRepository.findAll()).thenReturn(Collections.emptyList());

        List<TrainingTypeResponse> result = trainingTypeService.getAllTrainingTypes();

        assertThat(result).isEmpty();
        verify(trainingTypeRepository).findAll();
    }
}