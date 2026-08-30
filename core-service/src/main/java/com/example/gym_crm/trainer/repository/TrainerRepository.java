package com.example.gym_crm.trainer.repository;

import com.example.gym_crm.trainer.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrainerRepository extends JpaRepository<Trainer, UUID> {
    Optional<Trainer> findByUserUsername(String username);

    @Query("SELECT tr FROM Trainer tr " +
            "WHERE tr.user.isActive = true " +
            "AND tr NOT IN (" +
            "    SELECT t FROM Trainee trainee JOIN trainee.trainers t WHERE trainee.user.username = :username" +
            ")")
    List<Trainer> findActiveTrainersNotAssignedToTrainee(@Param("username") String username);

    @Query(
            "SELECT COUNT(tr) FROM Trainer tr WHERE tr.user.isActive = true"
    )
    Long countByUserIsActiveTrue();
}