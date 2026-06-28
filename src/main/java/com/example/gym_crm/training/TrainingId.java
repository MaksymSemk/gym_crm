package com.example.gym_crm.training;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public record TrainingId(
        UUID traineeId,
        UUID trainerId,
        LocalDate trainingDate
) {
    public TrainingId {
        Objects.requireNonNull(traineeId, "traineeId cannot be null");
        Objects.requireNonNull(trainerId, "trainerId cannot be null");
        Objects.requireNonNull(trainingDate, "trainingDate cannot be null");
    }
}