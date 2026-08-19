package com.example.gym_crm.common.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationErrorMetricsTest {

    private MeterRegistry meterRegistry;
    private ApplicationErrorMetrics errorMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        errorMetrics = new ApplicationErrorMetrics(meterRegistry);
    }

    @Test
    @DisplayName("Should not register counters in MeterRegistry prior to explicit error recording")
    void metrics_DoNotExistBeforeRecording() {
        Counter authCounter = meterRegistry.find("gym_crm_auth_failures_total")
                .tag("reason", "bad_credentials")
                .counter();

        Counter exceptionCounter = meterRegistry.find("gym_crm_exceptions_total")
                .tag("type", "EntityDoesNotExistException")
                .counter();

        assertNull(authCounter, "Auth failure counter should not exist before being triggered");
        assertNull(exceptionCounter, "Exception counter should not exist before being triggered");
    }

    @Test
    @DisplayName("Should increment authentication failure counter with specified reason tag")
    void recordAuthenticationFailure_IncrementsCounter() {
        // Assert absence prior to call
        assertNull(meterRegistry.find("gym_crm_auth_failures_total").tag("reason", "bad_credentials").counter());

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
        // Assert absence prior to call
        assertNull(meterRegistry.find("gym_crm_exceptions_total").tag("type", "EntityDoesNotExistException").counter());

        errorMetrics.recordException("EntityDoesNotExistException");

        Counter counter = meterRegistry.find("gym_crm_exceptions_total")
                .tag("type", "EntityDoesNotExistException")
                .counter();

        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    @DisplayName("Should isolate counters with different tags cleanly")
    void recordException_DifferentTags_MaintainsDistinctCounters() {
        errorMetrics.recordException("EntityDoesNotExistException");
        errorMetrics.recordException("IllegalArgumentException");

        Counter entityNotFoundCounter = meterRegistry.find("gym_crm_exceptions_total")
                .tag("type", "EntityDoesNotExistException")
                .counter();

        Counter illegalArgCounter = meterRegistry.find("gym_crm_exceptions_total")
                .tag("type", "IllegalArgumentException")
                .counter();

        assertNotNull(entityNotFoundCounter);
        assertNotNull(illegalArgCounter);

        assertEquals(1.0, entityNotFoundCounter.count());
        assertEquals(1.0, illegalArgCounter.count());
    }
}