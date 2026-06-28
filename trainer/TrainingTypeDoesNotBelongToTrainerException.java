package com.example.gym_crm.trainer;

public class TrainingTypeDoesNotBelongToTrainerException extends RuntimeException {
    public TrainingTypeDoesNotBelongToTrainerException(String message) {
        super(message);
    }
}
