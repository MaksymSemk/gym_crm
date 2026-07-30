package com.example.gym_crm.common.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityHealthIndicatorTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SecurityHealthIndicator healthIndicator;

    @Test
    @DisplayName("Should return Status.UP when password encoding and matching succeeds")
    void health_EncoderWorking_ReturnsUp() {
        when(passwordEncoder.encode("health_check_password_encoding")).thenReturn("encoded_pass");
        when(passwordEncoder.matches("health_check_password_encoding", "encoded_pass")).thenReturn(true);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("PasswordEncoder functioning normally", health.getDetails().get("status"));
    }

    @Test
    @DisplayName("Should return Status.DOWN when encoded password fails verification match")
    void health_EncoderMismatch_ReturnsDown() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_pass");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("PasswordEncoder verification failed", health.getDetails().get("error"));
    }

    @Test
    @DisplayName("Should return Status.DOWN when encoder throws exception")
    void health_EncoderThrowsException_ReturnsDown() {
        when(passwordEncoder.encode(anyString())).thenThrow(new RuntimeException("Cipher error"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}