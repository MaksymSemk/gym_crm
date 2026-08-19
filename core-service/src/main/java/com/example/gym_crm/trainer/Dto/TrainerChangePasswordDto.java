package com.example.gym_crm.trainer.Dto;

import com.example.gym_crm.authentication.AuthData;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = true)
@Data
public class TrainerChangePasswordDto extends AuthData {
    @NotBlank(message = "Old password is required")
    private String newPassword;
}