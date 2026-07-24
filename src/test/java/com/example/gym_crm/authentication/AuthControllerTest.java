package com.example.gym_crm.authentication;

import com.example.gym_crm.authentication.dto.ChangePasswordRequestDto;
import com.example.gym_crm.authentication.dto.LoginRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("GET /api/v1/auth/login - Success Returns 200 OK")
    void login_Success() throws Exception {
        doNothing().when(authService).login(any(LoginRequestDto.class), any(), any());

        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "john.doe")
                        .param("password", "secret123"))
                .andExpect(status().isOk());

        verify(authService).login(any(LoginRequestDto.class), any(), any());
    }

    @Test
    @DisplayName("GET /api/v1/auth/login - Missing Query Params Returns 400 Bad Request")
    void login_ValidationError() throws Exception {
        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "")
                        .param("password", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/auth/change-password - Success Returns 200 OK")
    void changePassword_Success() throws Exception {
        ChangePasswordRequestDto dto = new ChangePasswordRequestDto("john.doe", "oldPass", "newPass");
        doNothing().when(authService).changePassword(any(ChangePasswordRequestDto.class));

        mockMvc.perform(put("/api/v1/auth/change-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(authService).changePassword(any(ChangePasswordRequestDto.class));
    }
}