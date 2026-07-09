package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentification.AuthData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GetUnassignedTrainersDto extends AuthData {
    private String traineeUsername;

    public GetUnassignedTrainersDto(String username, String password, String traineeUsername) {
        super(username, password);
        this.traineeUsername = Objects.requireNonNull(traineeUsername, "traineeUsername is required");
    }
}