package com.example.gym_crm.trainee.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.Objects;

public record TraineeCreateDto(
        @NotBlank(message = "First name is required")
        String firstName,
        @NotBlank(message = "Last name is required")
        String lastName,
        @NotNull(message = "Active status is required")
        Boolean isActive,
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,
        String address
) {}
