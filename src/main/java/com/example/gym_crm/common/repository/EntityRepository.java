package com.example.gym_crm.common.repository;

import java.util.Collection;
import java.util.Optional;

public interface EntityRepository<ID, DAO> {

    Optional<DAO> findById(ID id);
    Collection<DAO> findAll();
    DAO create(DAO dao);
    DAO update(DAO dao);
    void delete(ID id);
    boolean existsById(ID id);

}
