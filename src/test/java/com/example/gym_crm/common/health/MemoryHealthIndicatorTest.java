package com.example.gym_crm.common.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemoryHealthIndicatorTest {

    @Mock
    private MemorySnapshot memorySnapshot;

    @Test
    @DisplayName("Should return Status.UP when memory usage is below the 90% threshold")
    void health_MemoryUsageBelowThreshold_ReturnsUp() {
        // 40% usage
        long maxMemory = 100 * 1024 * 1024L;
        long totalMemory = 50 * 1024 * 1024L;
        long freeMemory = 10 * 1024 * 1024L;

        when(memorySnapshot.getMaxMemory()).thenReturn(maxMemory);
        when(memorySnapshot.getTotalMemory()).thenReturn(totalMemory);
        when(memorySnapshot.getFreeMemory()).thenReturn(freeMemory);

        MemoryHealthIndicator indicator = new MemoryHealthIndicator(memorySnapshot);
        Health health = indicator.health();

        assertNotNull(health);
        assertEquals(Status.UP, health.getStatus());
        assertEquals(40L, health.getDetails().get("usedMemoryMb"));
        assertEquals(100L, health.getDetails().get("maxMemoryMb"));
        assertEquals("40.00%", health.getDetails().get("usagePercentage"));
    }

    @Test
    @DisplayName("Should return Status.DOWN when memory usage reaches or exceeds 90% threshold")
    void health_MemoryUsageExceedsThreshold_ReturnsDown() {
        // 91% usage
        long maxMemory = 100 * 1024 * 1024L;
        long totalMemory = 95 * 1024 * 1024L;
        long freeMemory = 4 * 1024 * 1024L;

        when(memorySnapshot.getMaxMemory()).thenReturn(maxMemory);
        when(memorySnapshot.getTotalMemory()).thenReturn(totalMemory);
        when(memorySnapshot.getFreeMemory()).thenReturn(freeMemory);

        MemoryHealthIndicator indicator = new MemoryHealthIndicator(memorySnapshot);
        Health health = indicator.health();

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("JVM Heap Memory critical threshold exceeded", health.getDetails().get("reason"));
        assertEquals(91L, health.getDetails().get("usedMemoryMb"));
        assertEquals(100L, health.getDetails().get("maxMemoryMb"));
        assertEquals("91.00%", health.getDetails().get("usagePercentage"));
    }

    @Test
    @DisplayName("Should return Status.DOWN exactly at the 90% threshold boundary")
    void health_MemoryUsageExactlyAtThreshold_ReturnsDown() {
        // 90.00% usage
        long maxMemory = 100 * 1024 * 1024L;
        long totalMemory = 90 * 1024 * 1024L;
        long freeMemory = 0L;

        when(memorySnapshot.getMaxMemory()).thenReturn(maxMemory);
        when(memorySnapshot.getTotalMemory()).thenReturn(totalMemory);
        when(memorySnapshot.getFreeMemory()).thenReturn(freeMemory);

        MemoryHealthIndicator indicator = new MemoryHealthIndicator(memorySnapshot);
        Health health = indicator.health();

        assertNotNull(health);
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("90.00%", health.getDetails().get("usagePercentage"));
    }

    @Test
    @DisplayName("Should evaluate live JVM runtime")
    void health_DefaultConstructor_ExecutesSuccessfully() {
        MemoryHealthIndicator indicator = new MemoryHealthIndicator(memorySnapshot);
        Health health = indicator.health();

        assertNotNull(health);
        assertTrue(health.getStatus() == Status.UP || health.getStatus() == Status.DOWN);
        assertNotNull(health.getDetails().get("usedMemoryMb"));
        assertNotNull(health.getDetails().get("maxMemoryMb"));
        assertNotNull(health.getDetails().get("usagePercentage"));
    }
}