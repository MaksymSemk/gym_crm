package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import com.example.gym_crm.common.user.PersonalIdentity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeUpdateDto extends AuthData implements PersonalIdentity {
    @NotNull(message = "User ID is required")
    private UUID userId;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String address;
    private List<UUID> training_ids;

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }
}
