package com.example.gym_crm.common.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseLatencyHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;
    private static final long LATENCY_THRESHOLD_MS = 1000; // 1 second

    @Override
    public Health health() {
        long startTime = System.currentTimeMillis();
        try {
            jdbcTemplate.execute("SELECT 1");
            long latency = System.currentTimeMillis() - startTime;

            if (latency > LATENCY_THRESHOLD_MS) {
                return Health.status("DEGRADED")
                        .withDetail("latencyMs", latency)
                        .withDetail("thresholdMs", LATENCY_THRESHOLD_MS)
                        .withDetail("message", "Database query execution latency is high")
                        .build();
            }

            return Health.up()
                    .withDetail("latencyMs", latency)
                    .withDetail("status", "Database connection pool responding normally")
                    .build();

        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("error", "Failed to execute database ping query")
                    .build();
        }
    }
}
