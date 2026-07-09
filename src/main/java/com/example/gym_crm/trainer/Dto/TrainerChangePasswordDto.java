package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentification.AuthData;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TrainerChangePasswordDto extends AuthData {
    @NotBlank(message = "Old password is required")
    private String newPassword;
}