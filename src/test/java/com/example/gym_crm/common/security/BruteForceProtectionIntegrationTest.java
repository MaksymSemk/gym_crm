package com.example.gym_crm.common.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BruteForceProtectionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("5 failed login attempts block the 6th attempt with HTTP 429")
    void login_MaxFailedAttempts_BlocksSubsequentAttempt() throws Exception {
        String username = "john.doe";
        String wrongPassword = "wrong_pass";

        // 5 Failed Attempts -> HTTP 401 Unauthorized
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/v1/auth/login")
                            .param("username", username)
                            .param("password", wrongPassword))
                    .andExpect(status().isUnauthorized());
        }

        // 6th Attempt -> HTTP 429 Too Many Requests
        mockMvc.perform(get("/api/v1/auth/login")
                        .param("username", username)
                        .param("password", wrongPassword))
                .andExpect(status().isTooManyRequests());
    }
}