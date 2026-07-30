package com.example.gym_crm.common.health;

import com.example.gym_crm.common.user.UserRepository;
import com.example.gym_crm.trainee.repository.TraineeRepository;
import com.example.gym_crm.trainer.repository.TrainerRepository;
import com.example.gym_crm.training.repository.TrainingRepository;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EntityCountHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Health health() {
        try {
            Map<String, Object> entityCounts = new LinkedHashMap<>();
            entityCounts.put("users", userRepository.count());
            entityCounts.put("trainees", traineeRepository.count());
            entityCounts.put("trainers", trainerRepository.count());
            entityCounts.put("trainings", trainingRepository.count());
            entityCounts.put("trainingTypes", trainingTypeRepository.count());

            return Health.up()
                    .withDetails(entityCounts)
                    .build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("error", "Failed to query database entity counts")
                    .withDetail("message", ex.getMessage())
                    .build();
        }
    }
}