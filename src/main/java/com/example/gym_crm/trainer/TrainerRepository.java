package com.example.gym_crm.trainer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, UUID> {
    Optional<Trainer> findByUserUsername(String username);

    @Query("SELECT t FROM Trainer t WHERE t.id NOT IN (" +
            "  SELECT tr.id FROM Trainee tn JOIN tn.trainers tr WHERE tn.user.username = :username" +
            ")")
    List<Trainer> findTrainersNotAssignedToTrainee(@Param("username") String username);
}