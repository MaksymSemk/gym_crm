package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeChangePasswordDto extends AuthData {
    private String newPassword;

    public TraineeChangePasswordDto(String username, String password, String newPassword) {
        super(username, password);
        this.newPassword = Objects.requireNonNull(newPassword, "newPassword is required");
    }
}
