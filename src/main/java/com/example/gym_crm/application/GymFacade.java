package com.example.gym_crm.application;

import com.example.gym_crm.authentication.AuthData;
import com.example.gym_crm.authentication.RequiresAuth;
import com.example.gym_crm.trainee.Dto.*;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeService;
import com.example.gym_crm.trainer.Dto.*;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerService;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Dto.TrainingGetByIdDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    // TRAINEE
    public Trainee createTrainee(@Valid TraineeCreateDto dto) {
        return traineeService.createTrainee(dto);
    }

    @RequiresAuth
    public Trainee updateTrainee(@Valid TraineeUpdateDto dto) {
        return traineeService.updateTrainee(dto);
    }

    @RequiresAuth
    public void deleteTrainee(@Valid TraineeDeleteDto dto) {
        traineeService.deleteTrainee(dto.getId());
    }

    @RequiresAuth
    public Trainee getTraineeById(@Valid TraineeGetByIdDto dto) {
        return traineeService.getTraineeById(dto.getId());
    }

    @RequiresAuth
    public Trainee getTraineeByUsername(@Valid AuthData authData) {
        return traineeService.getTraineeByUsername(authData.getUsername());
    }

    @RequiresAuth
    public Trainee changeTraineePassword(@Valid TraineeChangePasswordDto dto) {
        return traineeService.changePassword(dto);
    }

    @RequiresAuth
    public Trainee updateTraineeStatus(@Valid AuthData authData) {
        return traineeService.updateTraineeStatus(authData.getUsername());
    }

    @RequiresAuth
    public void deleteTraineeByUsername(@Valid AuthData authData) {
        traineeService.deleteTraineeByUsername(authData.getUsername());
    }

    @RequiresAuth
    public Trainee updateTraineeTrainers(@Valid TraineeUpdateTrainersDto dto) {
        return traineeService.updateTraineeTrainers(dto);
    }

    @RequiresAuth
    public List<Training> getTraineeTrainings(@Valid TraineeTrainingsSearchDto dto) {
        return traineeService.getTraineeTrainings(dto);
    }

    // TRAINER
    public Trainer createTrainer(@Valid TrainerCreateDto dto) {
        return trainerService.createTrainer(dto);
    }

    @RequiresAuth
    public Trainer updateTrainer(@Valid TrainerUpdateDto dto) {
        return trainerService.updateTrainer(dto);
    }

    @RequiresAuth
    public Trainer getTrainerById(@Valid TrainerGetByIdDto dto) {
        return trainerService.getTrainerByID(dto.getId());
    }

    @RequiresAuth
    public Trainer getTrainerByUsername(@Valid AuthData authData) {
        return trainerService.getTrainerByUsername(authData.getUsername());
    }

    @RequiresAuth
    public Trainer changeTrainerPassword(@Valid TrainerChangePasswordDto dto) {
        return trainerService.changePassword(dto);
    }

    @RequiresAuth
    public Trainer updateTrainerStatus(@Valid AuthData authData) {
        return trainerService.updateTrainerStatus(authData.getUsername());
    }

    @RequiresAuth
    public List<Training> getTrainerTrainings(@Valid TrainerTrainingsSearchDto dto) {
        return trainerService.getTrainerTrainings(dto);
    }

    @RequiresAuth
    public List<Trainer> getUnassignedTrainers(@Valid GetUnassignedTrainersDto dto) {
        return trainerService.getUnassignedTrainersByTraineeUsername(dto.getTraineeUsername());
    }

    // TRAINING
    @RequiresAuth
    public Training createTraining(@Valid TrainingCreateDto dto) {
        return trainingService.createTraining(dto);
    }

    @RequiresAuth
    public Training getTraining(@Valid TrainingGetByIdDto dto) {
        return trainingService.getTraining(dto.getId());
    }
}