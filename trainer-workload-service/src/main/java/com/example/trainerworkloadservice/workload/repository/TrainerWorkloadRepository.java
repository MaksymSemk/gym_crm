package com.example.trainerworkloadservice.workload.repository;

import com.example.trainerworkloadservice.workload.model.TrainerWorkload;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TrainerWorkloadRepository {

    private final Map<String, TrainerWorkload> storage = new ConcurrentHashMap<>();

    public Optional<TrainerWorkload> findByUsername(String username) {
        return Optional.ofNullable(storage.get(username));
    }

    public void save(TrainerWorkload workload) {
        storage.put(workload.getTrainerUsername(), workload);
    }
}