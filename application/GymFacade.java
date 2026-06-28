package com.example.gym_crm.application;


import com.example.gym_crm.trainee.TraineeService;
import com.example.gym_crm.trainer.TrainerService;
import com.example.gym_crm.training.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
}