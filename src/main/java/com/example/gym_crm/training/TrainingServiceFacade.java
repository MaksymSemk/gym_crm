package com.example.gym_crm.training;

import com.example.gym_crm.authentification.RequiresAuth;
import com.example.gym_crm.training.Dto.TrainingCreateDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrainingServiceFacade {

    private TrainingService trainingService;

    @Autowired
    public void setTrainingService(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @RequiresAuth
    public Training addTraining(@Valid TrainingCreateDto dto) {
        log.debug("Facade: Executing authenticated training creation logic flow");
        return trainingService.createTraining(dto);
    }
}