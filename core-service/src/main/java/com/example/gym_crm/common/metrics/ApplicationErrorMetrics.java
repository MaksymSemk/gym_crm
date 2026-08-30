package com.example.gym_crm.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationErrorMetrics {

    private final MeterRegistry registry;

    public void recordAuthenticationFailure(String reason) {
        Counter.builder("gym_crm_auth_failures_total")
                .tag("reason", reason)
                .description("Total number of failed authentication attempts")
                .register(registry)
                .increment();
    }

    public void recordException(String exceptionType) {
        Counter.builder("gym_crm_exceptions_total")
                .tag("type", exceptionType)
                .description("Total count of handled application exceptions")
                .register(registry)
                .increment();
    }
}