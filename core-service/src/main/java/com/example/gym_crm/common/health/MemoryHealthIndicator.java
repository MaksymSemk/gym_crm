package com.example.gym_crm.common.health;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MemoryHealthIndicator implements HealthIndicator {

    private final static double MEMORY_THRESHOLD = 0.9;

    private final MemorySnapshot memorySnapshot;

    public MemoryHealthIndicator(MemorySnapshot memorySnapshot) {
        this.memorySnapshot = memorySnapshot;
    }

    @Override
    public @Nullable Health health() {
        long maxMemory = memorySnapshot.getMaxMemory();
        long freeMemory = memorySnapshot.getFreeMemory();
        long totalMemory = memorySnapshot.getTotalMemory();
        long usedMemory = totalMemory - freeMemory;
        double memoryUsage = (double) usedMemory / maxMemory;

        long usedMb = usedMemory / (1024 * 1024);
        long maxMb = maxMemory / (1024 * 1024);

        if (memoryUsage >= MEMORY_THRESHOLD) {
            return Health.down()
                    .withDetail("reason", "JVM Heap Memory critical threshold exceeded")
                    .withDetail("usedMemoryMb", usedMb)
                    .withDetail("maxMemoryMb", maxMb)
                    .withDetail("usagePercentage", String.format("%.2f%%", memoryUsage * 100))
                    .build();
        }

        return Health.up()
                .withDetail("usedMemoryMb", usedMb)
                .withDetail("maxMemoryMb", maxMb)
                .withDetail("usagePercentage", String.format("%.2f%%", memoryUsage * 100))
                .build();
    }
}

