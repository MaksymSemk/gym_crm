package com.example.gym_crm.training_type;

import com.example.gym_crm.common.config.TestMetricsConfig;
import com.example.gym_crm.training_type.Dto.response.TrainingTypeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingTypeController.class)
@Import(TestMetricsConfig.class)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingTypeService trainingTypeService;

    @Test
    @DisplayName("GET /api/v1/training-types - Success Returns List of Training Types")
    void getTrainingTypes_Success() throws Exception {
        List<TrainingTypeResponse> mockTypes = List.of(
                new TrainingTypeResponse(1L, "Fitness"),
                new TrainingTypeResponse(2L, "Yoga")
        );

        when(trainingTypeService.getAllTrainingTypes()).thenReturn(mockTypes);

        mockMvc.perform(get("/api/v1/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trainingType").value("Fitness"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].trainingType").value("Yoga"));

        verify(trainingTypeService).getAllTrainingTypes();
    }
}