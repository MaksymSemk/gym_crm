package com.example.gym_crm.trainer;

import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;

import java.util.List;
import java.util.UUID;

public interface TrainerService {

    public Trainer getTrainerByID(UUID uuid);

    public Trainer createTrainer(TrainerCreateDto trainerCreateDto);

    public Trainer updateTrainer(TrainerUpdateDto trainerUpdateDto);

}
