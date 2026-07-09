package com.example.gym_crm.trainee;

import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;
import com.example.gym_crm.trainer.Trainer;

import java.util.List;
import java.util.UUID;

public interface TraineeService {

    Trainee createTrainee(TraineeCreateDto trainee);
    
    Trainee updateTrainee(TraineeUpdateDto trainee);
    
    void deleteTrainee(UUID id);
    
    Trainee getTraineeById(UUID id);
    
    Trainee getTraineeByUsername(String username);
    
    Trainee changePassword(String username, String newPassword);
    
    Trainee updateTraineeStatus(String username);
    
    void deleteTraineeByUsername(String username);
    
    Trainee updateTraineeTrainers(String username, List<Trainer> trainers);
}
