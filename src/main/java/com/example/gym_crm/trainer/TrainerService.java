package com.example.gym_crm.trainer;

import com.example.gym_crm.trainer.Dto.TrainerCreateDto;
import com.example.gym_crm.trainer.Dto.TrainerUpdateDto;
import com.example.gym_crm.training.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TrainerService {

    Trainer createTrainer(TrainerCreateDto dto);

    Trainer getTrainerByUsername(String username);

    Trainer updateTrainer(TrainerUpdateDto dto);

    Trainer changePassword(String username, String newPassword);

    Trainer updateTrainerStatus(String username);

    List<Training> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName);

    List<Trainer> getUnassignedTrainersByTraineeUsername(String traineeUsername);
}