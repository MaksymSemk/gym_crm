package com.example.gym_crm.common.config;

import com.example.gym_crm.common.metrics.ApplicationErrorMetrics;
import com.example.gym_crm.common.metrics.NoOpApplicationErrorMetrics;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestMetricsConfig {

    @Bean
    @Primary
    public ApplicationErrorMetrics applicationErrorMetrics() {
        return new NoOpApplicationErrorMetrics();
    }
}