package com.supera.enem.service;

import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class KeyClockUserServiceTest {

    @Mock
    private Keycloak keycloak;
    @Mock
    private UsersResource usersResource;

    @InjectMocks
    private KeycloackUserService keycloackUserService;

    @Test
    void testCreateUser_Success() {
        UseKeycloakRegistrationDTO dto = new UseKeycloakRegistrationDTO("john_doe", "john@example.com", "John", "Doe", "password123");
        Response response = mock(Response.class);
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId("12345");
        userRepresentation.setEmailVerified(false);

        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(201);
        when(usersResource.searchByUsername(eq(dto.getUsername()), eq(true)))
                .thenReturn(Collections.singletonList(userRepresentation));

        String userId = keycloackUserService.createUser(dto);

        assertEquals("12345", userId);
        verify(usersResource).create(any(UserRepresentation.class));
    }


}
