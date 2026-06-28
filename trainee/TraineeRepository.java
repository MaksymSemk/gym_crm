package com.example.gym_crm.trainee;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
public class TraineeRepository extends AbstractRepository<UUID, Trainee> {

    @Autowired
    @Override
    public void setStorage(Map<UUID, Trainee> traineeStorage) {
        super.setStorage(traineeStorage);
    }

    @Autowired
    @Override
    public void setIdGenerator(IdGenerator<UUID> uuidGenerator) {
        super.setIdGenerator(uuidGenerator);
    }
}
