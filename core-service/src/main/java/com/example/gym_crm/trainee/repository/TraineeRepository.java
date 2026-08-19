package com.example.gym_crm.trainee.repository;

import com.example.gym_crm.trainee.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TraineeRepository extends JpaRepository<Trainee, UUID> {
    Optional<Trainee> findByUserUsername(String username);

    @Query(
            "SELECT COUNT(tr) FROM Trainee tr WHERE tr.user.isActive = true"
    )
    long countByUserIsActiveTrue();
}