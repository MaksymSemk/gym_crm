package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentification.AuthData;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUnassignedTrainersDto extends AuthData {
    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

}