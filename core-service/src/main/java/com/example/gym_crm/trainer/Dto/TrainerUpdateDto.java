package com.example.gym_crm.trainer.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrainerUpdateDto(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotNull(message = "Is Active status is required")
        Boolean isActive
) {}