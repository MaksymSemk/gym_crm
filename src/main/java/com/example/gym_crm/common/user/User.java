package com.example.gym_crm.common.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

   @Column(nullable = false)
    private String firstName;

   @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isActive;

    public boolean updateIdentity(PersonalIdentity identity) {
        boolean updated = false;

        if (identity.getFirstName() != null && !identity.getFirstName().isBlank()
                && !identity.getFirstName().equals(this.firstName)) {
            this.firstName = identity.getFirstName();
            updated = true;
        }

        if (identity.getLastName() != null && !identity.getLastName().isBlank()
                && !identity.getLastName().equals(this.lastName)) {
            this.lastName = identity.getLastName();
            updated = true;
        }

        return updated;
    }

}

