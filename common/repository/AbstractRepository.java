package com.example.gym_crm.common.repository;

import com.example.gym_crm.common.id_generator.IdGenerator;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractRepository<ID, DAO extends EntityId<ID>> implements EntityRepository<ID, DAO> {

    private Map<ID, DAO> storage;
    private IdGenerator<ID> idGenerator;

    @Override
    public Optional<DAO> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Collection<DAO> findAll(){
        return storage.values();
    }

    @Override
    public DAO save(DAO dao) {
        dao.setId(idGenerator.generateNewId(dao));
        storage.put(dao.getId(), dao);
        return dao;
    }

    @Override
    public DAO update(DAO dao) {
        storage.put(dao.getId(), dao);
        return dao;
    }

    @Override
    public void delete(ID id) {
        storage.remove(id);
    }

    @Override
    public boolean existsById(ID id) {
        return storage.containsKey(id);
    }

    public void setStorage(Map<ID, DAO> storage) {
        this.storage=storage;
    }
    public void setIdGenerator(IdGenerator<ID> idGenerator) {
        this.idGenerator=idGenerator;
    }
}
