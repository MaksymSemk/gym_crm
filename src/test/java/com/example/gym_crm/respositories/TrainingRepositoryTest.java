package com.example.gym_crm.respositories;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training.TrainingRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TrainingRepositoryTest extends BaseRepositoryTest<TrainingId, Training> {

    @Mock
    private IdGenerator<TrainingId> trainingIdGenerator;

    @Override
    protected AbstractRepository<TrainingId, Training> createRepositoryInstance() {
        return new TrainingRepository();
    }

    @Override
    protected IdGenerator<TrainingId> createMockIdGenerator() {
        return trainingIdGenerator;
    }

    @Override
    protected TrainingId getTestId() {
        return new TrainingId(UUID.randomUUID(), UUID.randomUUID(), LocalDate.now());
    }

    @Override
    protected Training createTestEntity() {
        Training training = new Training();
        training.setTrainingName("Pilates Focus Block");
        return training;
    }
}