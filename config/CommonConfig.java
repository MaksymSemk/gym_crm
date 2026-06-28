package com.example.gym_crm.config;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.id_generator.StringIdGenerator;
import com.example.gym_crm.common.id_generator.UUIDGenerator;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class CommonConfig {

    @Bean
    public IdGenerator<UUID> uuidGenerator(){
        return new UUIDGenerator();
    }

    @Bean
    public IdGenerator<String> stringIdGenerator(){
        return new StringIdGenerator();
    }

    @Bean
    public IdGenerator<TrainingId> trainingIdGenerator() {
        return new TrainingIdGenerator();
    }
}
