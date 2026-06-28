package com.example.gym_crm.trainee.Dto;

import java.time.LocalDate;
import java.util.Objects;

public record TraineeCreateDto(
        String firstName,
        String lastName,
        Boolean isActive,
        LocalDate dateOfBirth,
        String address
) {
    public TraineeCreateDto{
        Objects.requireNonNull(firstName, "firstName is required");
        Objects.requireNonNull(lastName, "lastName is required");
        Objects.requireNonNull(isActive, "isActive is required");
        Objects.requireNonNull(dateOfBirth, "dateOfBirth is required");
        Objects.requireNonNull(address, "address is required");
    }
}
