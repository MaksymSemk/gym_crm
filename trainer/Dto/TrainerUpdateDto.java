package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.common.user.PersonalIdentity;
import com.example.gym_crm.training.TrainingId;
import com.example.gym_crm.training_type.TrainingType;

import java.util.Set;
import java.util.UUID;

public record TrainerUpdateDto(
        UUID userId,
        String firstName,
        String lastName,
        Boolean isActive,
        Set<TrainingType> specialization,
        Set<TrainingId> trainingIds
) implements PersonalIdentity
{
    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }
}
