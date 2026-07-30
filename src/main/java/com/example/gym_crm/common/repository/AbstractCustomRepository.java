package com.example.gym_crm.common.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public abstract class AbstractCustomRepository<T, ID> implements CustomCrudRepository<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityClass;

    protected AbstractCustomRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional
    @Override
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to save cannot be null");
        }
        if (entityManager.contains(entity)) {
            return entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
            return entity;
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return entityManager.createQuery(
                        "SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    @Transactional
    @Override
    public void deleteById(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID to delete cannot be null");
        }
        findById(id).ifPresent(this::delete);
    }

    @Transactional
    @Override
    public void delete(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity to delete cannot be null");
        }
        entityManager.remove(entityManager.contains(entity) ? entity : entityManager.merge(entity));
    }
}