package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import com.example.gym_crm.trainer.Trainer;
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
    private List<Trainer> trainers;

    public TraineeUpdateTrainersDto(String username, String password, List<Trainer> trainers) {
        super(username, password);
        this.trainers = Objects.requireNonNull(trainers, "trainers is required");
    }
}
