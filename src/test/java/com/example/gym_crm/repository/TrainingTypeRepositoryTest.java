package com.example.gym_crm.repository;

import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.training_type.repository.TrainingTypeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class TrainingTypeRepositoryTest extends BaseRepositoryTest {

    @Autowired private TrainingTypeRepository trainingTypeRepository;

    @Test
    @DisplayName("AbstractCustomRepository Lifecycle: Validate save, update tracking, selection lists, and removal execution")
    void customRepositoryLifecycle_ExecutionTrack() {
        TrainingType type = new TrainingType();
        type.setName("Weightlifting");
        TrainingType saved = trainingTypeRepository.save(type);
        assertNotNull(saved.getId());

        Optional<TrainingType> found = trainingTypeRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Weightlifting", found.get().getName());

        saved.setName("Olympic Weightlifting");
        TrainingType merged = trainingTypeRepository.save(saved);
        assertEquals("Olympic Weightlifting", merged.getName());

        List<TrainingType> items = trainingTypeRepository.findAll();
        assertFalse(items.isEmpty());

        trainingTypeRepository.deleteById(saved.getId());
        assertTrue(trainingTypeRepository.findById(saved.getId()).isEmpty());
    }

    @Test
    @DisplayName("AbstractCustomRepository Bounds: Throw targeted exceptions on illegal actions or invalid arguments")
    void customRepositoryLifecycle_ThrowsExceptionOnBadInputs() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> trainingTypeRepository.save(null));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> trainingTypeRepository.delete(null));
        assertThrows(InvalidDataAccessApiUsageException.class, () -> trainingTypeRepository.deleteById(null));

        Optional<TrainingType> absent = trainingTypeRepository.findById(null);
        assertTrue(absent.isEmpty());
    }
}