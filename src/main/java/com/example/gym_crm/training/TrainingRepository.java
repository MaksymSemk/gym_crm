package com.example.gym_crm.training;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingRepository extends AbstractRepository<TrainingId, Training> {

    @Autowired
    @Override
    public void setStorage(Map<TrainingId, Training> trainingStorage) {
        super.setStorage(trainingStorage);
    }

    @Autowired
    @Override
    public void setIdGenerator(IdGenerator<TrainingId> trainingIdGenerator) {
        super.setIdGenerator(trainingIdGenerator);
    }
}
