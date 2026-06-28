package com.example.gym_crm.trainer;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import com.example.gym_crm.trainee.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
public class TrainerRepository extends AbstractRepository<UUID, Trainer> {
    @Autowired
    @Override
    public void setStorage(Map<UUID, Trainer> trainerStorage) {
        super.setStorage(trainerStorage);
    }

    @Autowired
    @Override
    public void setIdGenerator(IdGenerator<UUID> uuidGenerator) {
        super.setIdGenerator(uuidGenerator);
    }
}
