package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentication.AuthData;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeUpdateTrainersDto extends AuthData {
    @NotEmpty(message = "Trainers list is required")
    private List<UUID> trainerIds;
}
