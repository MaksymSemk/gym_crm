package com.example.gym_crm.training.remote.dto;

import java.time.LocalDate;

public record TrainerWorkloadRequest(
        String trainerUsername,
        String trainerFirstName,
        String trainerLastName,
        Boolean isActive,
        LocalDate trainingDate,
        Integer trainingDuration,
        ActionType actionType
) {
    public enum ActionType {
        ADD,
        DELETE
    }
}