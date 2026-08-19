package com.example.gym_crm.trainee.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record TraineeUpdateTrainersDto(
        @NotBlank(message = "Trainee username is required")
        String traineeUsername,

        @NotEmpty(message = "Trainers list cannot be empty")
        List<TrainerUsernameDto> trainersList
) {
    public record TrainerUsernameDto(
            @NotBlank(message = "Trainer username is required")
            String username
    ) {}
}