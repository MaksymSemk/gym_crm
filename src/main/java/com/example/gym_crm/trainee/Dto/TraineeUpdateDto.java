package com.example.gym_crm.trainee.Dto;

import com.example.gym_crm.authentification.AuthData;
import com.example.gym_crm.common.user.PersonalIdentity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeUpdateDto extends AuthData implements PersonalIdentity {
    private UUID userId;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private LocalDate dateOfBirth;
    private String address;

    public TraineeUpdateDto(UUID userId, String firstName, String lastName, Boolean isActive, LocalDate dateOfBirth, String address, String username, String password) {
        super(username, password);
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = isActive;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }
}
