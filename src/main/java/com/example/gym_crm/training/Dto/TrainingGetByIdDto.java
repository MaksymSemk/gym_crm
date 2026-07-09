package com.example.gym_crm.training.Dto;

import com.example.gym_crm.authentification.AuthData;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainingGetByIdDto extends AuthData {
    @NotNull(message = "Training ID is required")
    private UUID id;
}