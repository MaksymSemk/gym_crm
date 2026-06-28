package com.example.gym_crm.trainer;

import com.example.gym_crm.common.repository.EntityId;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.training_type.TrainingType;
import com.example.gym_crm.common.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class Trainer extends User implements EntityId<UUID> {

    private Set<TrainingType> specialization;
    private UUID userId;
    private Set<Training> trainings;

    public Trainer(String firstName, String lastName, String username, String password, boolean isActive, Set<TrainingType> specialization) {
        super(firstName, lastName, username, password, isActive);
        this.specialization = specialization;
        this.userId = UUID.randomUUID();
        this.trainings = new HashSet<>();
    }

    @Override
    public UUID getId() {
        return userId;
    }

    @Override
    public void setId(UUID uuid) {
        this.userId = uuid;
    }
}
