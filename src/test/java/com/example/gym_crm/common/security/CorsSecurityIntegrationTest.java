package com.example.gym_crm.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CorsSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void optionsPreflight_AllowedOrigin_ReturnsCorsHeaders() throws Exception {
        mockMvc.perform(options("/api/v1/trainees")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void optionsPreflight_AllowedOrigin_ReturnsForbidden() throws Exception {
        mockMvc.perform(options("/api/v1/trainees")
                        .header("Origin", "http://localhost:5555")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden());
    }
}