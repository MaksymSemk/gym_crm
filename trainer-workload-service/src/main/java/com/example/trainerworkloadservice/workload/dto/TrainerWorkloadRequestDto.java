package com.example.trainerworkloadservice.workload.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TrainerWorkloadRequestDto(
        @NotBlank(message = "Trainer username is required")
        String trainerUsername,

        @NotBlank(message = "Trainer first name is required")
        String trainerFirstName,

        @NotBlank(message = "Trainer last name is required")
        String trainerLastName,

        @NotNull(message = "isActive flag is required")
        Boolean isActive,

        @NotNull(message = "Training date is required")
        LocalDate trainingDate,

        @NotNull(message = "Training duration is required")
        @Min(value = 1, message = "Duration must be at least 1 minute")
        Integer trainingDuration,

        @NotNull(message = "Action type (ADD/DELETE) is required")
        ActionType actionType
) {}
