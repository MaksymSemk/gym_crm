package com.example.gym_crm.common.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DatabaseLatencyHealthIndicatorTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DatabaseLatencyHealthIndicator healthIndicator;

    private static final String PING_QUERY = "SELECT 1";

    @Test
    @DisplayName("health() should return status UP when DB execution is fast")
    void health_ShouldReturnUp_WhenLatencyIsBelowThreshold() {
        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertNotNull(health.getDetails().get("latencyMs"));
        assertEquals("Database connection pool responding normally", health.getDetails().get("status"));
        verify(jdbcTemplate).execute(PING_QUERY);
    }

    @Test
    @DisplayName("health() should return status DEGRADED when DB execution exceeds threshold")
    void health_ShouldReturnDegraded_WhenLatencyExceedsThreshold() {
        doAnswer(invocation -> {
            Thread.sleep(1050);
            return null;
        }).when(jdbcTemplate).execute(anyString());

        Health health = healthIndicator.health();

        assertEquals(new Status("DEGRADED"), health.getStatus());
        assertEquals(1000L, health.getDetails().get("thresholdMs"));
        assertEquals("Database query execution latency is high", health.getDetails().get("message"));
        verify(jdbcTemplate).execute(PING_QUERY);
    }

    @Test
    @DisplayName("health() should return status DOWN when DB throws exception")
    void health_ShouldReturnDown_WhenQueryFails() {
        DataAccessException dbException = new DataAccessException("Database connection timeout") {};
        doThrow(dbException).when(jdbcTemplate).execute(anyString());

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Failed to execute database ping query", health.getDetails().get("error"));
        verify(jdbcTemplate).execute(PING_QUERY);
    }
}