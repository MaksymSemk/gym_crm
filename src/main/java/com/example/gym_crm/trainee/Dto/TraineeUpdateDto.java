package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.common.user.PersonalIdentity;
import com.example.gym_crm.training.TrainingId;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record TraineeUpdateDto(
        UUID userId,
        String firstName,
        String lastName,
        Boolean isActive,
        LocalDate dateOfBirth,
        String address,
        Set<TrainingId> trainingIds
)  implements PersonalIdentity
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
