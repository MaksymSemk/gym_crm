package com.example.gym_crm.training_type;

import com.example.gym_crm.common.repository.EntityId;
import lombok.Data;

@Data
public class TrainingType implements EntityId<String> {
    private String name;

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public void setId(String s) {
        this.name = s;
    }
}
