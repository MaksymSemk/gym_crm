package com.example.gym_crm.training.Dto;

import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training_type.TrainingType;

import java.time.LocalTime;
import java.util.Objects;
import java.util.Set;

public record TrainingUpdateDto(
        TrainingId trainingId,
        String trainingName,
        Set<TrainingType> trainingTypes,
        LocalTime trainingDuration
) {
    public TrainingUpdateDto {
        Objects.requireNonNull(trainingId, "trainingId is required");
    }
}
