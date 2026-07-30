package com.example.gym_crm.common.metrics;

import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActiveUsersGaugeTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;

    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
    }

    @Test
    @DisplayName("Should register active trainers and active trainees gauges in MeterRegistry")
    void gauges_RegisteredAndSampledCorrectly() {
        when(trainerRepository.countByUserIsActiveTrue()).thenReturn(15L);
        when(traineeRepository.countByUserIsActiveTrue()).thenReturn(45L);

        new ActiveUsersGauge(meterRegistry, trainerRepository, traineeRepository);

        Gauge trainerGauge = meterRegistry.find("gym_crm_active_trainers_count").gauge();
        Gauge traineeGauge = meterRegistry.find("gym_crm_active_trainees_count").gauge();

        assertNotNull(trainerGauge, "Trainer gauge should be registered");
        assertNotNull(traineeGauge, "Trainee gauge should be registered");

        assertEquals(15.0, trainerGauge.value());
        assertEquals(45.0, traineeGauge.value());

        verify(trainerRepository, times(1)).countByUserIsActiveTrue();
        verify(traineeRepository, times(1)).countByUserIsActiveTrue();
    }

    @Test
    @DisplayName("Should dynamically sample updated repository counts on consecutive reads")
    void gauges_ReflectDynamicCountChanges() {
        when(trainerRepository.countByUserIsActiveTrue()).thenReturn(10L);
        when(traineeRepository.countByUserIsActiveTrue()).thenReturn(20L);

        new ActiveUsersGauge(meterRegistry, trainerRepository, traineeRepository);

        Gauge trainerGauge = meterRegistry.find("gym_crm_active_trainers_count").gauge();
        Gauge traineeGauge = meterRegistry.find("gym_crm_active_trainees_count").gauge();

        assertNotNull(trainerGauge);
        assertNotNull(traineeGauge);
        assertEquals(10.0, trainerGauge.value());
        assertEquals(20.0, traineeGauge.value());

        when(trainerRepository.countByUserIsActiveTrue()).thenReturn(12L);
        when(traineeRepository.countByUserIsActiveTrue()).thenReturn(18L);

        assertEquals(12.0, trainerGauge.value());
        assertEquals(18.0, traineeGauge.value());
    }

    @Test
    @DisplayName("Should handle zero active users cleanly without errors")
    void gauges_ZeroCounts_ReturnsZero() {
        when(trainerRepository.countByUserIsActiveTrue()).thenReturn(0L);
        when(traineeRepository.countByUserIsActiveTrue()).thenReturn(0L);

        new ActiveUsersGauge(meterRegistry, trainerRepository, traineeRepository);

        Gauge trainerGauge = meterRegistry.find("gym_crm_active_trainers_count").gauge();
        Gauge traineeGauge = meterRegistry.find("gym_crm_active_trainees_count").gauge();

        assertNotNull(trainerGauge);
        assertNotNull(traineeGauge);
        assertEquals(0.0, trainerGauge.value());
        assertEquals(0.0, traineeGauge.value());
    }

    @Test
    @DisplayName("Should return NaN when repository throws an exception during scraping")
    void gauges_RepositoryException_ReturnsNaN() {
        when(trainerRepository.countByUserIsActiveTrue()).thenThrow(new RuntimeException("Database error"));
        when(traineeRepository.countByUserIsActiveTrue()).thenReturn(50L);

        new ActiveUsersGauge(meterRegistry, trainerRepository, traineeRepository);

        Gauge trainerGauge = meterRegistry.find("gym_crm_active_trainers_count").gauge();
        Gauge traineeGauge = meterRegistry.find("gym_crm_active_trainees_count").gauge();

        assertNotNull(trainerGauge);
        assertNotNull(traineeGauge);

        assertTrue(Double.isNaN(trainerGauge.value()));
        assertEquals(50.0, traineeGauge.value());
    }
}