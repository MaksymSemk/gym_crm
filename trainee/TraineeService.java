package com.example.gym_crm.trainee;

import com.example.gym_crm.trainee.Dto.TraineeCreateDto;
import com.example.gym_crm.trainee.Dto.TraineeUpdateDto;

import java.util.UUID;

public interface TraineeService {

    public Trainee createTrainee(TraineeCreateDto trainee);
    public Trainee updateTrainee(TraineeUpdateDto trainee);
    public void deleteTrainee(UUID id);

    public Trainee getTraineeById(UUID id);
}
