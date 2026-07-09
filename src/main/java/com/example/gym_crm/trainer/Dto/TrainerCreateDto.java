package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.training_type.TrainingType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record TrainerCreateDto(
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotNull(message = "Active status is required")
        Boolean isActive,
        @NotNull(message = "Specialization is required")
        TrainingType specialization
) {}
