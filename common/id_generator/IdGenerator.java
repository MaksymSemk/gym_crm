package com.example.gym_crm.common.id_generator;

public interface IdGenerator<ID> {
    ID generateNewId(Object entity);
}