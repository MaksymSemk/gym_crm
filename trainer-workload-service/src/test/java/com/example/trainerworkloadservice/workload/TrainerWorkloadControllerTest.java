package com.example.trainerworkloadservice.workload;

import com.example.trainerworkloadservice.security.JwtUtils;
import com.example.trainerworkloadservice.workload.dto.ActionType;
import com.example.trainerworkloadservice.workload.dto.TrainerWorkloadRequestDto;
import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainerWorkloadController.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerWorkloadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TrainerWorkloadService workloadService;

    @MockitoBean
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("POST /api/v1/workload returns 200 OK for valid payload")
    void acceptTrainerWorkload_Valid_Returns200() throws Exception {
        TrainerWorkloadRequestDto requestDto = new TrainerWorkloadRequestDto(
                "john.doe", "John", "Doe", true,
                LocalDate.of(2026, 8, 25), 60, ActionType.ADD
        );

        doNothing().when(workloadService).processWorkload(any());

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/workload returns 400 Bad Request when validation fails")
    void acceptTrainerWorkload_Invalid_Returns400() throws Exception {
        TrainerWorkloadRequestDto invalidDto = new TrainerWorkloadRequestDto(
                "", "", "", null, null, 0, null
        );

        mockMvc.perform(post("/api/v1/workload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/workload/{username} returns 200 with payload")
    void getTrainerWorkload_Returns200() throws Exception {
        TrainerWorkload workload = new TrainerWorkload("john.doe", "John", "Doe", true, new ArrayList<>());
        when(workloadService.getTrainerWorkload("john.doe")).thenReturn(workload);

        mockMvc.perform(get("/api/v1/workload/john.doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainerUsername").value("john.doe"))
                .andExpect(jsonPath("$.trainerFirstName").value("John"));
    }
}