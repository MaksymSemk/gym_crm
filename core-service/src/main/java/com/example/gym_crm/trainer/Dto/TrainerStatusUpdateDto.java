package com.example.gym_crm.trainer.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerStatusUpdateDto(
        @NotBlank(message = "Username is required")
        String username,
        @NotNull(message = "Is Active status is required")
        Boolean isActive
) {}