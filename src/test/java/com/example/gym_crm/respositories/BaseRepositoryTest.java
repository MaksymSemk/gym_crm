package com.example.gym_crm.respositories;

import com.example.gym_crm.common.id_generator.IdGenerator;
import com.example.gym_crm.common.repository.AbstractRepository;
import com.example.gym_crm.common.repository.EntityId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public abstract class BaseRepositoryTest<ID, DAO extends EntityId<ID>> {

    protected AbstractRepository<ID, DAO> repository;
    protected Map<ID, DAO> underlyingStorage;
    protected IdGenerator<ID> mockIdGenerator;

    protected abstract AbstractRepository<ID, DAO> createRepositoryInstance();
    protected abstract IdGenerator<ID> createMockIdGenerator();
    protected abstract ID getTestId();
    protected abstract DAO createTestEntity();

    @BeforeEach
    void baseSetUp() {
        repository = createRepositoryInstance();
        underlyingStorage = new HashMap<>();
        mockIdGenerator = createMockIdGenerator();

        repository.setStorage(underlyingStorage);
        repository.setIdGenerator(mockIdGenerator);
    }

    @Test
    @DisplayName("Should generate a fresh ID value and store entity upon executing save mutations")
    protected void save_ShouldGenerateIdAndStoreEntity() {
        ID expectedId = getTestId();
        DAO entity = createTestEntity();

        when(mockIdGenerator.generateNewId(entity)).thenReturn(expectedId);

        DAO savedEntity = repository.create(entity);

        assertNotNull(savedEntity);
        assertEquals(expectedId, savedEntity.getId());
        assertTrue(underlyingStorage.containsKey(expectedId));
        verify(mockIdGenerator, times(1)).generateNewId(entity);
    }

    @Test
    @DisplayName("Should extract entity correctly when matched against a populated storage reference key")
    protected void findById_ShouldReturnEntityWhenKeyExists() {
        ID id = getTestId();
        DAO entity = createTestEntity();
        entity.setId(id);
        underlyingStorage.put(id, entity);

        Optional<DAO> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    @DisplayName("Should produce empty optional variant when querying non-existent indexing keys")
    protected void findById_ShouldReturnEmptyOptionalWhenKeyIsMissing() {
        ID missingId = getTestId();

        Optional<DAO> result = repository.findById(missingId);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should completely pull elements out of reference boundaries during delete sequence calls")
    protected void delete_ShouldRemoveEntityFromStorage() {
        ID id = getTestId();
        DAO entity = createTestEntity();
        entity.setId(id);
        underlyingStorage.put(id, entity);

        repository.delete(id);

        assertFalse(underlyingStorage.containsKey(id));
    }

    @Test
    @DisplayName("Should confirm accurate containment tracking states during system key queries")
    protected void existsById_ShouldReturnTrueWhenEntityExists() {
        ID id = getTestId();
        DAO entity = createTestEntity();
        entity.setId(id);
        underlyingStorage.put(id, entity);

        boolean exists = repository.existsById(id);

        assertTrue(exists);
    }
}