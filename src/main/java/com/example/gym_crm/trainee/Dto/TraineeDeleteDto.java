package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeDeleteDto extends AuthData {
    private UUID id;

    public TraineeDeleteDto(String username, String password, UUID id) {
        super(username, password);
        this.id = id;
    }
}
