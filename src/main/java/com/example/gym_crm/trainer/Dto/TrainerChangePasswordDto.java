package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentification.AuthData;
import lombok.Getter;

@Getter
public class TrainerChangePasswordDto extends AuthData {
    private final String newPassword;

    public TrainerChangePasswordDto(String username, String password, String newPassword) {
        super(username, password);
        this.newPassword = newPassword;
    }
}