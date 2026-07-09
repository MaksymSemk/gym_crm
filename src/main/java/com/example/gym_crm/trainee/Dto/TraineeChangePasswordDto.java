package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentication.AuthData;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeChangePasswordDto extends AuthData {
    @NotBlank(message = "New password is required")
    private String newPassword;

    public TraineeChangePasswordDto(String username, String password, String newPassword) {
        this.setUsername(username);
        this.setPassword(password);
        this.newPassword = newPassword;
    }
}
