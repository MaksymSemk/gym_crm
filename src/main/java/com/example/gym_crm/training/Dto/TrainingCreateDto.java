package com.example.gym_crm.training.Dto;

import com.example.gym_crm.authentication.AuthData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.time.LocalDate;

@Getter
public class TrainingCreateDto extends AuthData {
    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;
    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;
    @NotBlank(message = "Training name is required")
    private String trainingName;
    @NotNull(message = "Training date is required")
    private LocalDate trainingDate;
    @NotNull(message = "Training duration is required")
    private Integer trainingDuration;
}