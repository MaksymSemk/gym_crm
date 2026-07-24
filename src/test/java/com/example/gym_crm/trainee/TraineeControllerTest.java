package com.example.gym_crm.trainee;

import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeStatusUpdateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateTrainersDto;
import com.example.gym_crm.trainee.Dto.responce.TraineeCreatedResponse;
import com.example.gym_crm.trainee.Dto.responce.TraineeGetResponse;
import com.example.gym_crm.trainee.Dto.responce.TraineeTrainingResponse;
import com.example.gym_crm.trainee.Dto.responce.TrainerGetResponse;
import com.example.gym_crm.trainee.mapper.TraineeMapper;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.training.Training;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeController.class)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private TraineeService traineeService;

    @MockitoBean
    private TraineeMapper traineeMapper;

    @Test
    @DisplayName("POST /api/v1/trainees - Success")
    void createTrainee_Success() throws Exception {
        TraineeCreateDto createDto = new TraineeCreateDto(
                "John", "Doe", LocalDate.of(1995, 5, 20), "123 Main St"
        );
        Trainee mockTrainee = new Trainee();
        TraineeCreatedResponse responseDto = new TraineeCreatedResponse("John.Doe", "generatedPass123");

        when(traineeService.createTrainee(any(TraineeCreateDto.class))).thenReturn(mockTrainee);
        when(traineeMapper.toTraineeCreatedResponse(mockTrainee)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").value("generatedPass123"));

        verify(traineeService).createTrainee(any(TraineeCreateDto.class));
    }

    @Test
    @DisplayName("POST /api/v1/trainees - Validation Error when Required Fields Missing")
    void createTrainee_ValidationError() throws Exception {
        TraineeCreateDto invalidDto = new TraineeCreateDto(
                "", "", LocalDate.of(1995, 5, 20), "123 Main St"
        );

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/trainees/{username} - Success")
    void getTraineeProfile_Success() throws Exception {
        String username = "john.doe";
        Trainee mockTrainee = new Trainee();
        TraineeGetResponse responseDto = new TraineeGetResponse(
                "John", "Doe", LocalDate.of(1995, 5, 20), "123 St", true, List.of()
        );

        when(traineeService.getTraineeByUsername(username)).thenReturn(mockTrainee);
        when(traineeMapper.toTraineeGetResponse(mockTrainee)).thenReturn(responseDto);

        mockMvc.perform(get("/api/v1/trainees/{username}", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(traineeService).getTraineeByUsername(username);
    }

    @Test
    @DisplayName("PUT /api/v1/trainees - Success")
    void updateTraineeProfile_Success() throws Exception {
        TraineeUpdateDto updateDto = new TraineeUpdateDto(
                "john.doe", "John", "Doe", LocalDate.of(1995, 5, 20), "456 St", true
        );
        Trainee mockTrainee = new Trainee();
        TraineeGetResponse responseDto = new TraineeGetResponse(
                "John", "Doe", LocalDate.of(1995, 5, 20), "456 St", true, List.of()
        );

        when(traineeService.updateTrainee(any(TraineeUpdateDto.class))).thenReturn(mockTrainee);
        when(traineeMapper.toTraineeGetResponse(mockTrainee)).thenReturn(responseDto);

        mockMvc.perform(put("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("456 St"));

        verify(traineeService).updateTrainee(any(TraineeUpdateDto.class));
    }

    @Test
    @DisplayName("PUT /api/v1/trainees - Validation Error when Required Fields Missing")
    void updateTraineeProfile_ValidationError() throws Exception {
        TraineeUpdateDto invalidDto = new TraineeUpdateDto(
                "", "", "Doe", LocalDate.of(1995, 5, 20), "456 St", true
        );

        mockMvc.perform(put("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/v1/trainees/{username} - Success")
    void deleteTraineeProfile_Success() throws Exception {
        String username = "john.doe";
        doNothing().when(traineeService).deleteTraineeByUsername(username);

        mockMvc.perform(delete("/api/v1/trainees/{username}", username))
                .andExpect(status().isOk());

        verify(traineeService).deleteTraineeByUsername(username);
    }

    @Test
    @DisplayName("GET /api/v1/trainees/{username}/unassigned-trainers - Success")
    void getUnassignedActiveTrainers_Success() throws Exception {
        String username = "john.doe";
        Trainer mockTrainer = new Trainer();
        TrainerGetResponse trainerDto = new TrainerGetResponse("trainer1", "Alice", "Smith", 1L);

        when(traineeService.getUnassignedActiveTrainers(username)).thenReturn(List.of(mockTrainer));
        when(traineeMapper.toTrainerGetResponseList(List.of(mockTrainer))).thenReturn(List.of(trainerDto));

        mockMvc.perform(get("/api/v1/trainees/{username}/unassigned-trainers", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("trainer1"))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[0].specializationId").value(1));

        verify(traineeService).getUnassignedActiveTrainers(username);
    }

    @Test
    @DisplayName("PUT /api/v1/trainees/trainers - Success")
    void updateTraineeTrainers_Success() throws Exception {
        TraineeUpdateTrainersDto.TrainerUsernameDto trainerUsername = new TraineeUpdateTrainersDto.TrainerUsernameDto("trainer1");
        TraineeUpdateTrainersDto dto = new TraineeUpdateTrainersDto("john.doe", List.of(trainerUsername));

        Trainer mockTrainer = new Trainer();
        Trainee mockTrainee = Trainee.builder().trainers(List.of(mockTrainer)).build();
        TrainerGetResponse trainerDto = new TrainerGetResponse("trainer1", "Alice", "Smith", 1L);

        when(traineeService.updateTraineeTrainers(any(TraineeUpdateTrainersDto.class))).thenReturn(mockTrainee);
        when(traineeMapper.toTrainerGetResponseList(mockTrainee.getTrainers())).thenReturn(List.of(trainerDto));

        mockMvc.perform(put("/api/v1/trainees/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("trainer1"));

        verify(traineeService).updateTraineeTrainers(any(TraineeUpdateTrainersDto.class));
    }

    @Test
    @DisplayName("GET /api/v1/trainees/{username}/trainings - Success with Parameters")
    void getTraineeTrainings_Success() throws Exception {
        String username = "john.doe";
        Training mockTraining = new Training();
        TraineeTrainingResponse trainingResponse = new TraineeTrainingResponse(
                "Morning Cardio", LocalDate.of(2026, 3, 1), "Fitness", 60, "Alice Smith"
        );

        when(traineeService.getTraineeTrainings(any())).thenReturn(List.of(mockTraining));
        when(traineeMapper.toTraineeTrainingResponseList(List.of(mockTraining))).thenReturn(List.of(trainingResponse));

        mockMvc.perform(get("/api/v1/trainees/{username}/trainings", username)
                        .param("periodFrom", "2026-01-01")
                        .param("periodTo", "2026-03-31")
                        .param("trainerName", "Alice")
                        .param("trainingType", "Fitness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Cardio"))
                .andExpect(jsonPath("$[0].trainingDuration").value(60))
                .andExpect(jsonPath("$[0].trainerName").value("Alice Smith"));

        verify(traineeService).getTraineeTrainings(any());
    }

    @Test
    @DisplayName("PATCH /api/v1/trainees/status - Success")
    void toggleTraineeStatus_Success() throws Exception {
        TraineeStatusUpdateDto dto = new TraineeStatusUpdateDto("john.doe", false);

        mockMvc.perform(patch("/api/v1/trainees/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(traineeService).updateTraineeStatus("john.doe");
    }
}