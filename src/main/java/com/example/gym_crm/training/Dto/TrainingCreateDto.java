package com.example.gym_crm.training.Dto;

import com.example.gym_crm.authentification.AuthData;
import lombok.Getter;
import java.time.LocalDate;
import java.util.Objects;

@Getter
public class TrainingCreateDto extends AuthData {
    private final String traineeUsername;
    private final String trainerUsername;
    private final String trainingName;
    private final LocalDate trainingDate;
    private final Integer trainingDuration;

    public TrainingCreateDto(String authUsername, String authPassword, String traineeUsername,
                             String trainerUsername, String trainingName, LocalDate trainingDate, Integer trainingDuration) {
        super(authUsername, authPassword);
        this.traineeUsername = Objects.requireNonNull(traineeUsername, "traineeUsername is required");
        this.trainerUsername = Objects.requireNonNull(trainerUsername, "trainerUsername is required");
        this.trainingName = Objects.requireNonNull(trainingName, "trainingName is required");
        this.trainingDate = Objects.requireNonNull(trainingDate, "trainingDate is required");
        this.trainingDuration = Objects.requireNonNull(trainingDuration, "trainingDuration is required");

        if (trainingDuration <= 0) {
            throw new IllegalArgumentException("Training duration must be a positive number");
        }
    }
}