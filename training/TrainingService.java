package com.example.gym_crm.training;

import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Dto.TrainingUpdateDto;

public interface TrainingService {

    Training createTraining(TrainingCreateDto trainingCreateDto);
    Training getTraining(TrainingId id);
}
