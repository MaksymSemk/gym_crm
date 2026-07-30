package com.example.gym_crm.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationErrorMetricsTest {

    private MeterRegistry meterRegistry;
    private ApplicationErrorMetrics errorMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        errorMetrics = new ApplicationErrorMetrics(meterRegistry);
    }

    @Test
    @DisplayName("Should increment authentication failure counter with specified reason tag")
    void recordAuthenticationFailure_IncrementsCounter() {
        errorMetrics.recordAuthenticationFailure("bad_credentials");
        errorMetrics.recordAuthenticationFailure("bad_credentials");

        Counter counter = meterRegistry.find("gym_crm_auth_failures_total")
                .tag("reason", "bad_credentials")
                .counter();

        assertNotNull(counter);
        assertEquals(2.0, counter.count());
    }

    @Test
    @DisplayName("Should increment exceptions counter with specified type tag")
    void recordException_IncrementsCounter() {
        errorMetrics.recordException("EntityDoesNotExistException");

        Counter counter = meterRegistry.find("gym_crm_exceptions_total")
                .tag("type", "EntityDoesNotExistException")
                .counter();

        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }
}