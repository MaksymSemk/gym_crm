package com.example.gym_crm.trainee;

public class TrainingDoesNotBelongToTraineeException extends RuntimeException {
    public TrainingDoesNotBelongToTraineeException(String message) {
        super(message);
    }
}
