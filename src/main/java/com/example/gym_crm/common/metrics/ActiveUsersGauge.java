package com.example.gym_crm.common.metrics;

import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ActiveUsersGauge {

    public ActiveUsersGauge(MeterRegistry registry, TrainerRepository trainerRepository, TraineeRepository traineeRepository) {
        Gauge.builder("gym_crm_active_trainers_count", trainerRepository,
                        TrainerRepository::countByUserIsActiveTrue)
                .description("Current number of active trainers in the system")
                .register(registry);

        Gauge.builder("gym_crm_active_trainees_count", traineeRepository,
                        TraineeRepository::countByUserIsActiveTrue)
                .description("Current number of active trainees in the system")
                .register(registry);
    }
}