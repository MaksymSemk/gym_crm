package com.example.gym_crm.common.id_generator;

import java.util.UUID;

public class StringIdGenerator implements IdGenerator<String> {

    @Override
    public String generateNewId(Object entity) {
        return UUID.randomUUID().toString();
    }
}
