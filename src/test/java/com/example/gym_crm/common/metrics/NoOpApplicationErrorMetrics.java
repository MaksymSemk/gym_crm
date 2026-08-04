package com.example.gym_crm.common.metrics;

public class NoOpApplicationErrorMetrics extends ApplicationErrorMetrics {

    public NoOpApplicationErrorMetrics() {
        super(null);
    }

    @Override
    public void recordAuthenticationFailure(String reason) {
        // No-op
    }

    @Override
    public void recordException(String exceptionType) {
        // No-op
    }
}