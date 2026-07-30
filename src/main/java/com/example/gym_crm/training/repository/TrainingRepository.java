package com.example.gym_crm.training.repository;

import com.example.gym_crm.common.repository.CustomCrudRepository;
import com.example.gym_crm.training.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TrainingRepository extends CustomCrudRepository<Training, UUID> {

    List<Training> findTrainerTrainingsByCriteria(
            String username,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    );

    List<Training> findTraineeTrainingsByCriteria(
            String username,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingType
    );
}