package com.example.gym_crm.training;

import com.example.gym_crm.common.config.TestMetricsConfig;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingController.class)
@Import(TestMetricsConfig.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private TrainingService trainingService;

    @Test
    @DisplayName("POST /api/v1/trainings - Success Returns 200 OK")
    void addTraining_Success() throws Exception {
        TrainingCreateDto createDto = new TrainingCreateDto(
                "john.doe",
                "Alice.Smith",
                "Morning Cardio",
                LocalDate.of(2026, 3, 20),
                60
        );

        when(trainingService.createTraining(any(TrainingCreateDto.class))).thenReturn(new Training());

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk());

        verify(trainingService).createTraining(any(TrainingCreateDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/trainings - Validation Error Returns 400 Bad Request")
    void addTraining_ValidationError() throws Exception {
        TrainingCreateDto invalidDto = new TrainingCreateDto("", "", "", null, null);

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}