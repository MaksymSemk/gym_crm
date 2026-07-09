package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import com.example.gym_crm.trainer.Trainer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeUpdateTrainersDto extends AuthData {
    @NotEmpty(message = "Trainers list is required")
    private List<Trainer> trainers;
}
