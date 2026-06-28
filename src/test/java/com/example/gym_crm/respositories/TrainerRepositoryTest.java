package com.example.gym_crm.respositories;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import com.example.gym_crm.trainer.Trainer;
import com.example.gym_crm.trainer.TrainerRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryTest extends BaseRepositoryTest<UUID, Trainer> {

    @Mock
    private IdGenerator<UUID> uuidGenerator;

    @Override
    protected AbstractRepository<UUID, Trainer> createRepositoryInstance() {
        return new TrainerRepository();
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
    protected Trainer createTestEntity() {
        Trainer trainer = new Trainer();
        trainer.setLastName("Smith");
        return trainer;
    }
}