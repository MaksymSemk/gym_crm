package com.example.gym_crm.training;

import com.example.gym_crm.training.Dto.TrainingCreateDto;

import java.util.UUID;

public interface TrainingService {
    Training createTraining(TrainingCreateDto dto);

    Training getTraining(UUID id);
}