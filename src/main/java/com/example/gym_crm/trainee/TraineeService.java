package com.example.gym_crm.trainee;

import com.example.gym_crm.trainee.Dto.*;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.training.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TraineeService {
    Trainee createTrainee(TraineeCreateDto trainee);
    Trainee updateTrainee(TraineeUpdateDto trainee);
    void deleteTrainee(UUID id);
    Trainee getTraineeById(UUID id);
    Trainee getTraineeByUsername(String username);
    Trainee changePassword(TraineeChangePasswordDto dto);
    Trainee updateTraineeStatus(String username);
    void deleteTraineeByUsername(String username);
    Trainee updateTraineeTrainers(TraineeUpdateTrainersDto dto);
    List<Training> getTraineeTrainings(TraineeTrainingsSearchDto dto);
}
