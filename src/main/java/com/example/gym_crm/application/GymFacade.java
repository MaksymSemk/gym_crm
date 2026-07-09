package com.example.gym_crm.application;

import com.example.gym_crm.authentification.AuthData;
import com.example.gym_crm.trainee.Dto.*;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeServiceFacade;
import com.example.gym_crm.trainer.Dto.*;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerServiceFacade;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import com.example.gym_crm.training.Dto.TrainingGetByIdDto;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingServiceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeServiceFacade traineeServiceFacade;
    private final TrainerServiceFacade trainerServiceFacade;
    private final TrainingServiceFacade trainingServiceFacade;


    // TRAINEE
    public Trainee createTrainee(TraineeCreateDto dto) {
        return traineeServiceFacade.createTrainee(dto);
    }

    public Trainee updateTrainee(TraineeUpdateDto dto) {
        return traineeServiceFacade.updateTrainee(dto);
    }

    public void deleteTrainee(TraineeDeleteDto dto) {
        traineeServiceFacade.deleteTrainee(dto);
    }

    public Trainee getTraineeById(TraineeGetByIdDto dto) {
        return traineeServiceFacade.getTraineeById(dto);
    }

    public Trainee getTraineeByUsername(AuthData authData) {
        return traineeServiceFacade.getTraineeByUsername(authData);
    }

    public Trainee changeTraineePassword(TraineeChangePasswordDto dto) {
        return traineeServiceFacade.changePassword(dto);
    }

    public Trainee updateTraineeStatus(AuthData authData) {
        return traineeServiceFacade.updateTraineeStatus(authData);
    }

    public void deleteTraineeByUsername(AuthData authData) {
        traineeServiceFacade.deleteTraineeByUsername(authData);
    }

    public Trainee updateTraineeTrainers(TraineeUpdateTrainersDto dto) {
        return traineeServiceFacade.updateTraineeTrainers(dto);
    }

    public List<Training> getTraineeTrainings(TraineeTrainingsSearchDto dto) {
        return traineeServiceFacade.getTraineeTrainings(dto);
    }

    // TRAINER
    public Trainer createTrainer(TrainerCreateDto dto) {
        return trainerServiceFacade.createTrainer(dto);
    }

    public Trainer updateTrainer(TrainerUpdateDto dto) {
        return trainerServiceFacade.updateTrainer(dto);
    }

    public Trainer getTrainerById(TrainerGetByIdDto dto) {
        return trainerServiceFacade.getTrainerByID(dto);
    }

    public Trainer getTrainerByUsername(AuthData authData) {
        return trainerServiceFacade.getTrainerByUsername(authData);
    }

    public Trainer changeTrainerPassword(TrainerChangePasswordDto dto) {
        return trainerServiceFacade.changePassword(dto);
    }

    public Trainer updateTrainerStatus(AuthData authData) {
        return trainerServiceFacade.updateTrainerStatus(authData);
    }

    public List<Training> getTrainerTrainings(TrainerTrainingsSearchDto dto) {
        return trainerServiceFacade.getTrainerTrainings(dto);
    }

    public List<Trainer> getUnassignedTrainers(GetUnassignedTrainersDto dto) {
        return trainerServiceFacade.getUnassignedTrainers(dto);
    }

    // TRAINING
    public Training createTraining(TrainingCreateDto dto) {
        return trainingServiceFacade.addTraining(dto);
    }

    public Training getTraining(TrainingGetByIdDto dto) {
        return trainingServiceFacade.getTraining(dto);
    }
}