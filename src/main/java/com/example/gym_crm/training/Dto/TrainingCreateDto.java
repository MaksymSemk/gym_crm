package com.example.gym_crm.training.Dto;

import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training_type.TrainingType;

import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

public record TrainingCreateDto(
        TrainingId trainingId,
        String trainingName,
        Set<TrainingType> trainingTypes,
        LocalTime trainingDuration
) {
    public TrainingCreateDto {
        Objects.requireNonNull(trainingId, "trainingId is required");
        Objects.requireNonNull(trainingName, "trainingName is required");
        Objects.requireNonNull(trainingTypes, "trainingTypes is required");
        Objects.requireNonNull(trainingDuration, "trainingDuration is required");
    }
}
