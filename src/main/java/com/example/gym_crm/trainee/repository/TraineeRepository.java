package com.example.gym_crm.trainee.repository;

import com.example.gym_crm.common.repository.CustomCrudRepository;
import com.example.gym_crm.trainee.Trainee;

import java.util.Optional;
import java.util.UUID;

public interface TraineeRepository extends CustomCrudRepository<Trainee, UUID> {
    Optional<Trainee> findByUserUsername(String username);
}