package com.example.gym_crm.trainer.repository;

import com.example.gym_crm.common.repository.CustomCrudRepository;
import com.example.gym_crm.trainer.Trainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainerRepository extends CustomCrudRepository<Trainer, UUID> {
    Optional<Trainer> findByUserUsername(String username);
    List<Trainer> findTrainersNotAssignedToTrainee(String username);
}