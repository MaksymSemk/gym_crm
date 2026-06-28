package com.example.gym_crm.common.id_generator;

import java.util.UUID;

public class UUIDGenerator implements IdGenerator<UUID> {

    @Override
    public UUID generateNewId(Object object) {
        return UUID.randomUUID();
    }
}
