package com.example.gym_crm.application;

import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeService;
import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerService;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    //Trainee
    public Trainee createTrainee(TraineeCreateDto dto) {return traineeService.createTrainee(dto);}
    public Trainee updateTrainee(TraineeUpdateDto dto) {return traineeService.updateTrainee(dto);}
    public void deleteTrainee(UUID id) {traineeService.deleteTrainee(id);}
    public Trainee getTraineeById(UUID id) {return traineeService.getTraineeById(id);}

    //Trainer
    public Trainer createTrainer(TrainerCreateDto dto) {return trainerService.createTrainer(dto);}
    public Trainer updateTrainer(TrainerUpdateDto dto) {return trainerService.updateTrainer(dto);}
    public Trainer getTrainerById(UUID id) {return trainerService.getTrainerByID(id);}

    //Training
    public Training createTraining(TrainingCreateDto dto) {return trainingService.createTraining(dto);}
    public Training getTraining(TrainingId id) {return trainingService.getTraining(id);}
}