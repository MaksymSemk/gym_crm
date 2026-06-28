package com.example.gym_crm.respositories;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import com.example.gym_crm.trainee.Trainee;
import com.example.gym_crm.trainee.TraineeRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryTest extends BaseRepositoryTest<UUID, Trainee> {

    @Mock
    private IdGenerator<UUID> uuidGenerator;

    @Override
    protected AbstractRepository<UUID, Trainee> createRepositoryInstance() {
        return new TraineeRepository();
    }

    @Override
    protected IdGenerator<UUID> createMockIdGenerator() {
        return uuidGenerator;
    }

    @Override
    protected UUID getTestId() {
        return UUID.randomUUID();
    }

    @Override
    protected Trainee createTestEntity() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("Alice");
        return trainee;
    }
}