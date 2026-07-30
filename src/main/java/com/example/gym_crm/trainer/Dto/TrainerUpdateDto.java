package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentication.AuthData;
import com.example.gym_crm.common.user.PersonalIdentity;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainerUpdateDto extends AuthData implements PersonalIdentity {
    @NotNull(message = "User ID is required")
    private UUID userId;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private Long specializationId;
}