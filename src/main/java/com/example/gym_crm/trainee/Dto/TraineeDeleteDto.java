package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentication.AuthData;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeDeleteDto extends AuthData {
    @NotNull(message = "Trainee ID is required")
    private UUID id;
}
