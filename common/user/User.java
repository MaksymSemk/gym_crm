package com.example.gym_crm.common.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private String firstName;

    private String lastName;

    private String username;

    private String password;

    private boolean isActive;

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

