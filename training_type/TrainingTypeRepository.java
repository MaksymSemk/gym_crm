package com.example.gym_crm.training_type;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class TrainingTypeRepository extends AbstractRepository<String, TrainingType> {
    @Autowired
    @Override
    public void setStorage(Map<String, TrainingType> trainingTypeStorage) {
        super.setStorage(trainingTypeStorage);
    }

    @Autowired
    @Override
    public void setIdGenerator(IdGenerator<String> stringIdGenerator) {
        super.setIdGenerator(stringIdGenerator);
    }
}