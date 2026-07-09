package com.example.gym_crm.trainee;

import com.example.gym_crm.authentification.AuthData;
import com.example.gym_crm.authentification.RequiresAuth;
import com.example.gym_crm.trainee.Dto.*;
import com.example.gym_crm.trainer.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class TraineeServiceFacade {

    private TraineeService traineeService;

    @Autowired
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public Trainee createTrainee(TraineeCreateDto trainee) {
        log.debug("Facade: Creating trainee via service");
        return traineeService.createTrainee(trainee);
    }

    @RequiresAuth
    public Trainee updateTrainee(TraineeUpdateDto trainee) {
        log.debug("Facade: Updating trainee via service");
        return traineeService.updateTrainee(trainee);
    }

    @RequiresAuth
    public void deleteTrainee(TraineeDeleteDto traineeDeleteDto) {
        log.debug("Facade: Deleting trainee via service");
        traineeService.deleteTrainee(traineeDeleteDto.getId());
    }

    @RequiresAuth
    public Trainee getTraineeById(TraineeGetByIdDto id) {
        log.debug("Facade: Retrieving trainee by ID via service");
        return traineeService.getTraineeById(id.getId());
    }

    @RequiresAuth
    public Trainee getTraineeByUsername(AuthData authData) {
        log.debug("Facade: Retrieving trainee by username with authentication");
        return traineeService.getTraineeByUsername(authData.getUsername());
    }

    @RequiresAuth
    public Trainee changePassword(TraineeChangePasswordDto dto) {
        log.debug("Facade: Changing password for trainee with authentication");
        return traineeService.changePassword(dto.getUsername(), dto.getNewPassword());
    }

    @RequiresAuth
    public Trainee updateTraineeStatus(AuthData authData) {
        log.debug("Facade: Updating trainee status with authentication");
        return traineeService.updateTraineeStatus(authData.getUsername());
    }

    @RequiresAuth
    public void deleteTraineeByUsername(AuthData authData) {
        log.debug("Facade: Deleting trainee by username with authentication");
        traineeService.deleteTraineeByUsername(authData.getUsername());
    }

    @RequiresAuth
    public Trainee updateTraineeTrainers(TraineeUpdateTrainersDto dto) {
        log.debug("Facade: Updating trainee trainers with authentication");
        return traineeService.updateTraineeTrainers(dto.getUsername(), dto.getTrainers());
    }
}
