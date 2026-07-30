package com.example.gym_crm.trainee.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TraineeUpdateDto(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        LocalDate dateOfBirth,

        String address,

        @NotNull(message = "Is Active status is required")
        Boolean isActive
) {}