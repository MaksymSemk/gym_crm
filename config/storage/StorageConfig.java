package com.example.gym_crm.config.storage;

import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainer.Trainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Configuration
public class StorageConfig {

    @Bean(name= "trainerStorage")
    public Map<UUID, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean(name= "traineeStorage")
    public Map<UUID, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean(name= "trainingTypeStorage")
    public Map<String, TrainingType> trainingTypeStorage() {
        return new HashMap<>();
    }

    @Bean(name= "trainingStorage")
    public Map<TrainingId, Training> trainingStorage() {
        return new HashMap<>();
    }
}
