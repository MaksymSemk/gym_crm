package com.example.gym_crm.trainee.Dto.responce;

import java.time.LocalDate;

public record TraineeTrainingResponse(
        String trainingName,
        LocalDate trainingDate,
        String trainingType,
        Integer trainingDuration,
        String trainerName
) {}