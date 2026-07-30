package com.example.gym_crm.trainer;

import com.example.gym_crm.trainer.Dto.*;
import com.example.gym_crm.training.Training;

import java.util.List;
import java.util.UUID;

public interface TrainerService {
    Trainer createTrainer(TrainerCreateDto dto);
    Trainer getTrainerByUsername(String username);
    Trainer updateTrainer(TrainerUpdateDto dto);
    Trainer changePassword(TrainerChangePasswordDto dto);
    Trainer updateTrainerStatus(String username, Boolean newStatus);
    List<Training> getTrainerTrainings(TrainerTrainingsSearchDto dto);
    List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername);
    Trainer getTrainerByID(UUID uuid);
}