package com.supera.enem.service;

import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;

public class KeyClockUserServiceTest {

    @InjectMocks
    private KeycloackUserService keycloackUserService;

    private UseKeycloakRegistrationDTO userDTO;

    @BeforeEach
    public void setUp() {
        userDTO = new UseKeycloakRegistrationDTO();
        userDTO.setUsername("testuser");
        userDTO.setEmail("test@example.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");
        userDTO.setPassword("password123");


    }

    @Test
    @DisplayName("Test createUser")
    public void testCreateUser() {

    }

}
