package com.example.gym_crm.trainer;

public class TrainingDoesNotBelongToTrainerException extends RuntimeException {
    public TrainingDoesNotBelongToTrainerException(String message) {
        super(message);
    }
}
