package com.example.gym_crm.training_type.repository;

import com.example.gym_crm.common.repository.AbstractCustomRepository;
import com.example.gym_crm.training_type.TrainingType;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeRepositoryImpl extends AbstractCustomRepository<TrainingType, Long> implements TrainingTypeRepository {

    public TrainingTypeRepositoryImpl() {
        super(TrainingType.class);
    }
}