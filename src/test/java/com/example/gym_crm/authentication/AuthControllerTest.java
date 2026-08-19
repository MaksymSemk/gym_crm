package com.example.gym_crm.authentication;

import com.example.gym_crm.authentication.dto.AuthResponseDto;
import com.example.gym_crm.authentication.dto.ChangePasswordRequestDto;
import com.example.gym_crm.authentication.dto.LoginRequestDto;
import com.example.gym_crm.common.BaseControllerTest;
import com.example.gym_crm.common.config.TestMetricsConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(TestMetricsConfig.class)
class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @MockitoBean
    private AuthService authService;
    @Test
    @DisplayName("GET /api/v1/auth/login - Success Returns 200 OK")
    void login_Success() throws Exception {
        AuthResponseDto mockResponse = new AuthResponseDto("john.doe", "mocked-jwt-token");
        when(authService.login(any(LoginRequestDto.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", "john.doe")
                        .param("password", "secret123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));

        verify(authService).login(any(LoginRequestDto.class));
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

    @Test
    @DisplayName("POST /api/v1/auth/logout - Success Returns 200 OK")
    void logout_Success() throws Exception {
        doNothing().when(authService).logout(any(HttpServletRequest.class));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logged out successfully"));

        verify(authService).logout(any(HttpServletRequest.class));
    }
}