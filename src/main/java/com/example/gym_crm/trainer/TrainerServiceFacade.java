package com.example.gym_crm.trainer;

import com.example.gym_crm.authentification.AuthData;
import com.example.gym_crm.authentification.RequiresAuth;
import com.example.gym_crm.trainer.Dto.*;
import com.example.gym_crm.training.Training;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TrainerServiceFacade {

    private TrainerService trainerService;

    @Autowired
    public void setTrainerService(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public Trainer createTrainer(@Valid TrainerCreateDto dto) {
        log.debug("Facade: Creating trainer profile");
        return trainerService.createTrainer(dto);
    }

    @RequiresAuth
    public Trainer getTrainerByUsername(@Valid AuthData authData) {
        log.debug("Facade: Fetching trainer profile by username with authentication");
        return trainerService.getTrainerByUsername(authData.getUsername());
    }

    @RequiresAuth
    public Trainer updateTrainer(@Valid TrainerUpdateDto dto) {
        log.debug("Facade: Updating trainer profile with authentication");
        return trainerService.updateTrainer(dto);
    }

    @RequiresAuth
    public Trainer changePassword(@Valid TrainerChangePasswordDto dto) {
        log.debug("Facade: Modifying trainer password with authentication");
        return trainerService.changePassword(dto.getUsername(), dto.getNewPassword());
    }

    @RequiresAuth
    public Trainer updateTrainerStatus(@Valid AuthData authData) {
        log.debug("Facade: Toggling trainer status with authentication");
        return trainerService.updateTrainerStatus(authData.getUsername());
    }

    @RequiresAuth
    public List<Training> getTrainerTrainings(@Valid TrainerTrainingsSearchDto dto) {
        log.debug("Facade: Querying trainer trainings history with authentication");
        return trainerService.getTrainerTrainings(
                dto.getUsername(),
                dto.getFromDate(),
                dto.getToDate(),
                dto.getTraineeName()
        );
    }

    @RequiresAuth
    public List<Trainer> getUnassignedTrainers(@Valid GetUnassignedTrainersDto dto) {
        log.debug("Facade: Querying unassigned trainers list via trainee context");
        return trainerService.getUnassignedTrainersByTraineeUsername(dto.getTraineeUsername());
    }

    @RequiresAuth
    public Trainer getTrainerByID(@Valid TrainerGetByIdDto dto) {
        log.debug("Facade: Authenticating and retrieving trainer by ID via service");
        return trainerService.getTrainerByID(dto.getId());
    }
}