package com.example.gym_crm.trainer;

import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerStatusUpdateDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.trainer.Dto.response.TraineeInfoDto;
import com.example.gym_crm.trainer.Dto.response.TrainerCreatedResponse;
import com.example.gym_crm.trainer.Dto.response.TrainerGetResponse;
import com.example.gym_crm.trainer.Dto.response.TrainerTrainingResponse;
import com.example.gym_crm.trainer.mapper.TrainerMapper;
import com.example.gym_crm.training.Training;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerController.class)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private TrainerService trainerService;

    @MockitoBean
    private TrainerMapper trainerMapper;

    // ------------------------------------------------------------------------
    // 1. Trainer Registration (POST /api/v1/trainers)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("POST /api/v1/trainers - Success")
    void registerTrainer_Success() throws Exception {
        TrainerCreateDto createDto = new TrainerCreateDto("Alice", "Smith", 1L);
        Trainer mockTrainer = new Trainer();
        TrainerCreatedResponse responseDto = new TrainerCreatedResponse("Alice.Smith", "pass123");

        when(trainerService.createTrainer(any(TrainerCreateDto.class))).thenReturn(mockTrainer);
        when(trainerMapper.toTrainerCreatedResponse(mockTrainer)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Alice.Smith"))
                .andExpect(jsonPath("$.password").value("pass123"));

        verify(trainerService).createTrainer(any(TrainerCreateDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/trainers - Validation Error")
    void registerTrainer_ValidationError() throws Exception {
        TrainerCreateDto invalidDto = new TrainerCreateDto("", "", null);

        mockMvc.perform(post("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------------
    // 2. Get Trainer Profile (GET /api/v1/trainers/{username})
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("GET /api/v1/trainers/{username} - Success")
    void getTrainerProfile_Success() throws Exception {
        String username = "Alice.Smith";
        Trainer mockTrainer = new Trainer();
        TraineeInfoDto traineeInfoDto = new TraineeInfoDto("john.doe", "John", "Doe");
        TrainerGetResponse responseDto = new TrainerGetResponse(
                username, "Alice", "Smith", "Fitness", true, List.of(traineeInfoDto)
        );

        when(trainerService.getTrainerByUsername(username)).thenReturn(mockTrainer);
        when(trainerMapper.toTrainerGetResponse(mockTrainer)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/trainers/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.specialization").value("Fitness"))
                .andExpect(jsonPath("$.traineesList[0].username").value("john.doe"));

        verify(trainerService).getTrainerByUsername(username);
    }

    // ------------------------------------------------------------------------
    // 3. Update Trainer Profile (PUT /api/v1/trainers)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("PUT /api/v1/trainers - Success")
    void updateTrainerProfile_Success() throws Exception {
        TrainerUpdateDto updateDto = new TrainerUpdateDto("Alice.Smith", "Alice", "Johnson", true);
        Trainer mockTrainer = new Trainer();
        TrainerGetResponse responseDto = new TrainerGetResponse(
                "Alice.Smith", "Alice", "Johnson", "Fitness", true, List.of()
        );

        when(trainerService.updateTrainer(any(TrainerUpdateDto.class))).thenReturn(mockTrainer);
        when(trainerMapper.toTrainerGetResponse(mockTrainer)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Johnson"));

        verify(trainerService).updateTrainer(any(TrainerUpdateDto.class));
    }

    // ------------------------------------------------------------------------
    // 4. Get Trainer Trainings List (GET /api/v1/trainers/{username}/trainings)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("GET /api/v1/trainers/{username}/trainings - Success")
    void getTrainerTrainings_Success() throws Exception {
        String username = "Alice.Smith";
        Training mockTraining = new Training();
        TrainerTrainingResponse responseDto = new TrainerTrainingResponse(
                "Morning Workout", LocalDate.of(2026, 3, 15), "Fitness", 45, "John Doe"
        );

        when(trainerService.getTrainerTrainings(any())).thenReturn(List.of(mockTraining));
        when(trainerMapper.toTrainerTrainingResponseList(List.of(mockTraining))).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/api/v1/trainers/{username}/trainings", username)
                        .param("periodFrom", "2026-01-01")
                        .param("periodTo", "2026-03-31")
                        .param("traineeName", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Workout"))
                .andExpect(jsonPath("$[0].traineeName").value("John Doe"));

        verify(trainerService).getTrainerTrainings(any());
    }

    // ------------------------------------------------------------------------
    // 5. Activate/De-Activate Trainer (PATCH /api/v1/trainers/status)
    // ------------------------------------------------------------------------
    @Test
    @DisplayName("PATCH /api/v1/trainers/status - Success")
    void toggleTrainerStatus_Success() throws Exception {
        TrainerStatusUpdateDto statusDto = new TrainerStatusUpdateDto("Alice.Smith", false);

        mockMvc.perform(patch("/api/v1/trainers/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk());

        verify(trainerService).updateTrainerStatus("Alice.Smith");
    }
}