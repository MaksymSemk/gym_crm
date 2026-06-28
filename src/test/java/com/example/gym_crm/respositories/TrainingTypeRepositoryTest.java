package com.example.gym_crm.respositories;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.TrainingTypeRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingTypeRepositoryTest extends BaseRepositoryTest<String, TrainingType> {

    @Mock
    private IdGenerator<String> stringIdGenerator;

    @Override
    protected AbstractRepository<String, TrainingType> createRepositoryInstance() {
        return new TrainingTypeRepository();
    }

    @Override
    protected IdGenerator<String> createMockIdGenerator() {
        return stringIdGenerator;
    }

    @Override
    protected String getTestId() {
        return "Weightlifting";
    }

    @Override
    protected TrainingType createTestEntity() {
        TrainingType trainingType = new TrainingType();
        trainingType.setName("Weightlifting");
        return trainingType;
    }
}