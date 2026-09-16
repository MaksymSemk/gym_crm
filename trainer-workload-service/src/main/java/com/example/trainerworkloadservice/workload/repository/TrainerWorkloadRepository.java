package com.example.trainerworkloadservice.workload.repository;

import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerWorkloadRepository extends MongoRepository<TrainerWorkload, String> {

    Optional<TrainerWorkload> findByTrainerUsername(String trainerUsername);

    default Optional<TrainerWorkload> findByUsername(String username) {
        return findById(username);
    }

    List<TrainerWorkload> findByTrainerFirstNameAndTrainerLastName(String trainerFirstName, String trainerLastName);
}