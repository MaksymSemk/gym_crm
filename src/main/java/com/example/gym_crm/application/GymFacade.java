package com.example.gym_crm.application;

import com.example.gym_crm.authentication.AuthData;
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

    public Trainee updateTrainee(@Valid TraineeUpdateDto dto) {
        return traineeService.updateTrainee(dto);
    }

    public void deleteTrainee(@Valid TraineeDeleteDto dto) {
        traineeService.deleteTrainee(dto.getId());
    }

    public Trainee getTraineeById(@Valid TraineeGetByIdDto dto) {
        return traineeService.getTraineeById(dto.getId());
    }

    public Trainee getTraineeByUsername(@Valid AuthData authData) {
        return traineeService.getTraineeByUsername(authData.getUsername());
    }

    public Trainee changeTraineePassword(@Valid TraineeChangePasswordDto dto) {
        return traineeService.changePassword(dto);
    }

    public Trainee updateTraineeStatus(@Valid AuthData authData, boolean status) {
        return traineeService.updateTraineeStatus(authData.getUsername(), status);
    }

    public void deleteTraineeByUsername(@Valid AuthData authData) {
        traineeService.deleteTraineeByUsername(authData.getUsername());
    }

    public Trainee updateTraineeTrainers(@Valid TraineeUpdateTrainersDto dto) {
        return traineeService.updateTraineeTrainers(dto);
    }

    public List<Training> getTraineeTrainings(@Valid TraineeTrainingsSearchDto dto) {
        return traineeService.getTraineeTrainings(dto);
    }

    // TRAINER
    public Trainer createTrainer(@Valid TrainerCreateDto dto) {
        return trainerService.createTrainer(dto);
    }

    public Trainer updateTrainer(@Valid TrainerUpdateDto dto) {
        return trainerService.updateTrainer(dto);
    }

    public Trainer getTrainerById(@Valid TrainerGetByIdDto dto) {
        return trainerService.getTrainerByID(dto.getId());
    }

    public Trainer getTrainerByUsername(@Valid AuthData authData) {
        return trainerService.getTrainerByUsername(authData.getUsername());
    }

    public Trainer changeTrainerPassword(@Valid TrainerChangePasswordDto dto) {
        return trainerService.changePassword(dto);
    }

    public Trainer updateTrainerStatus(@Valid AuthData authData, Boolean status) {
        return trainerService.updateTrainerStatus(authData.getUsername(), status);
    }

    public List<Training> getTrainerTrainings(@Valid TrainerTrainingsSearchDto dto) {
        return trainerService.getTrainerTrainings(dto);
    }

    public List<Trainer> getUnassignedTrainers(@Valid GetUnassignedTrainersDto dto) {
        return trainerService.getUnassignedTrainersByTraineeUsername(dto.getTraineeUsername());
    }

    // TRAINING
    public Training createTraining(@Valid TrainingCreateDto dto) {
        return trainingService.createTraining(dto);
    }

    public Training getTraining(@Valid TrainingGetByIdDto dto) {
        return trainingService.getTraining(dto.getId());
    }
}