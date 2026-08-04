package com.example.gym_crm.common.metrics;

import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.repository.TrainingRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class EntityMetrics {

    public EntityMetrics(
            MeterRegistry registry,
            UserRepository userRepository,
            TraineeRepository traineeRepository,
            TrainerRepository trainerRepository,
            TrainingRepository trainingRepository) {

        Gauge.builder("gym_crm_entities_total", userRepository, UserRepository::count)
                .tag("entity", "user")
                .description("Total number of user entities in database")
                .register(registry);

        Gauge.builder("gym_crm_entities_total", traineeRepository, TraineeRepository::count)
                .tag("entity", "trainee")
                .description("Total number of trainee entities in database")
                .register(registry);

        Gauge.builder("gym_crm_entities_total", trainerRepository, TrainerRepository::count)
                .tag("entity", "trainer")
                .description("Total number of trainer entities in database")
                .register(registry);

        Gauge.builder("gym_crm_entities_total", trainingRepository, TrainingRepository::count)
                .tag("entity", "training")
                .description("Total number of training entities in database")
                .register(registry);
    }
}