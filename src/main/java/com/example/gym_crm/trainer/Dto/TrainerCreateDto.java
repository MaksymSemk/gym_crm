package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.training_type.TrainingType;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record TrainerCreateDto(
        String firstName,
        String lastName,
        Boolean isActive,
        TrainingType specialization
) {
    public TrainerCreateDto {
        Objects.requireNonNull(firstName, "firstName is required");
        Objects.requireNonNull(lastName, "lastName is required");
        Objects.requireNonNull(isActive, "isActive is required");
        Objects.requireNonNull(specialization, "specialization is required");
    }
}
