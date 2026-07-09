package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentification.AuthData;
import com.example.gym_crm.common.user.PersonalIdentity;
import lombok.Getter;
import java.util.UUID;

@Getter
public class TrainerUpdateDto extends AuthData implements PersonalIdentity {
    private final UUID userId;
    private final String firstName;
    private final String lastName;
    private final Boolean isActive;
    private final Long specializationId;

    public TrainerUpdateDto(String username, String password, UUID userId, String firstName, String lastName, Boolean isActive, Long specializationId) {
        super(username, password);
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.specializationId = specializationId;
    }
}