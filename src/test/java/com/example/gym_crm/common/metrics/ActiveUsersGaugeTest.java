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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

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

        assertNotNull(trainerGauge);
        assertNotNull(traineeGauge);

        assertEquals(15.0, trainerGauge.value());
        assertEquals(45.0, traineeGauge.value());
    }
}