package com.example.gym_crm.common.repository;

import java.util.List;
import java.util.Optional;

public interface CustomCrudRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void delete(T entity);
}