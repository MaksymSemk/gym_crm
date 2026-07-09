package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "New password is required")
    private String newPassword;
}
