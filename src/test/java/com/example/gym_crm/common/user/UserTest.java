package com.example.gym_crm.common.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("John.Doe")
                .build();
    }

    @Test
    @DisplayName("Should return true and update fields when names are valid and distinct")
    void updateIdentity_ValidNewNames_UpdatesAndReturnsTrue() {
        PersonalIdentity identity = mock(PersonalIdentity.class);
        when(identity.getFirstName()).thenReturn("Johnny");
        when(identity.getLastName()).thenReturn("Smith");

        boolean result = user.updateIdentity(identity);

        assertTrue(result);
        assertEquals("Johnny", user.getFirstName());
        assertEquals("Smith", user.getLastName());
    }

    @Test
    @DisplayName("Should return false and not modify fields when incoming fields match current state")
    void updateIdentity_IdenticalNames_ReturnsFalse() {
        PersonalIdentity identity = mock(PersonalIdentity.class);
        when(identity.getFirstName()).thenReturn("John");
        when(identity.getLastName()).thenReturn("Doe");

        boolean result = user.updateIdentity(identity);

        assertFalse(result);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
    }

    @Test
    @DisplayName("Should ignore updating properties when inputs are blank or null strings")
    void updateIdentity_NullOrBlankNames_NoChange() {
        PersonalIdentity identity = mock(PersonalIdentity.class);
        when(identity.getFirstName()).thenReturn(" ");
        when(identity.getLastName()).thenReturn(null);

        boolean result = user.updateIdentity(identity);

        assertFalse(result);
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
    }
}