package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeGetByIdDto extends AuthData {
    @NotNull(message = "Trainee ID is required")
    private UUID id;
}
