package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentification.AuthData;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerGetByIdDto extends AuthData {
    @NotNull(message = "Trainer ID is required")
    private UUID id;
}