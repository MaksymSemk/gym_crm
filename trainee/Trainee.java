package com.example.gym_crm.trainee;

import com.example.gym_crm.common.repository.EntityId;
import com.example.gym_crm.training.Training;
import com.example.gym_crm.common.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class Trainee extends User implements EntityId<UUID> {

    private LocalDate dateOfBirth;
    private String address;
    private UUID userId;
    private Set<Training> trainings;

    public Trainee(String firstName, String lastName, String username, String password, boolean isActive, LocalDate dateOfBirth, String address, UUID userId) {
        super(firstName, lastName, username, password, isActive);
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.userId = userId;
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
