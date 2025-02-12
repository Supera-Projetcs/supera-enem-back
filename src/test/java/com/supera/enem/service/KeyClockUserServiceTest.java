package com.supera.enem.service;

import com.supera.enem.controller.DTOS.Student.UpdatePasswordDTO;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.exception.ResourceAlreadyExists;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.TokenVerifier;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class KeyClockUserServiceTest {

    @Mock
    private Keycloak keycloak;

    @Mock
    private UsersResource usersResource;

    @Mock
    private RealmResource realmResource;

    @InjectMocks
    private KeycloackUserService keycloackUserService;

    private String realm = "supera_enem";

    @BeforeEach
    public void setUp() {
        keycloackUserService.setRealm(realm);
        when(keycloak.realm(realm)).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
    }

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

    @Test
    void testUpdateEmail_Success() {
        String userId = "12345";
        String newEmail = "new_email@example.com";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(newEmail);
        userRepresentation.setEmailVerified(false);

        UserResource userResource = mock(UserResource.class); // Mock the UserResource
        when(usersResource.get(userId)).thenReturn(userResource); // Return the mocked userResource
        when(userResource.toRepresentation()).thenReturn(userRepresentation); // Return the mocked UserRepresentation

        keycloackUserService.updateEmail(userId, newEmail);

        verify(userResource).update(userRepresentation);
    }

    @Test
    void testUpdateEmail_UserNotFound() {
        String userId = "nonexistent";
        String newEmail = "new_email@example.com";

        when(usersResource.get(userId)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            keycloackUserService.updateEmail(userId, newEmail);
        });

        assertEquals("Usuário não encontrado no keycloak.", thrown.getMessage());
    }

    @Test
    void testUpdateUsername_Success() {
        String userId = "12345";
        String username = "new_username";
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(username);

        UserResource userResource = mock(UserResource.class); // Mock the UserResource
        when(usersResource.get(userId)).thenReturn(userResource); // Return the mocked userResource
        when(userResource.toRepresentation()).thenReturn(userRepresentation); // Return the mocked UserRepresentation

        keycloackUserService.updateUsername(userId, username);

        verify(userResource).update(userRepresentation);
    }

    @Test
    void testUpdateUsername_UserNotFound() {
        String userId = "nonexistent";
        String username = "new_username";

        when(usersResource.get(userId)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            keycloackUserService.updateUsername(userId, username);
        });

        assertEquals("Usuário não encontrado no keycloak.", thrown.getMessage());
    }

    @Test
    void testUpdatePassword_Success() {
        String userId = "12345";
        UpdatePasswordDTO passwordDTO = new UpdatePasswordDTO("newPassword123");

        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(userId)).thenReturn(userResource);
        keycloackUserService.updatePassword(userId, passwordDTO);

        ArgumentCaptor<CredentialRepresentation> captor = ArgumentCaptor.forClass(CredentialRepresentation.class);
        verify(userResource).resetPassword(captor.capture());

        assertEquals(passwordDTO.getNewPassword(), captor.getValue().getValue());
    }

    @Test
    void testUpdateEmail_InvalidEmail() {
        String userId = "12345";
        String invalidEmail = "invalid-email";

        UserResource userResource = mock(UserResource.class);
        UserRepresentation userRepresentation = new UserRepresentation();
        when(usersResource.get(userId)).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(userRepresentation);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> keycloackUserService.updateEmail(userId, invalidEmail));
        assertEquals("O e-mail fornecido é inválido.", exception.getMessage());
    }

    @Test
    void testUpdateEmail_EmptyEmail() {
        String userId = "12345";
        String emptyEmail = "";

        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(userId)).thenReturn(userResource);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> keycloackUserService.updateEmail(userId, emptyEmail));
        assertEquals("O e-mail fornecido é inválido.", exception.getMessage());
    }

    @Test
    void testUpdateUsername_InvalidUsername() {
        // Arrange
        String userId = "12345";
        String invalidUsername = "";

        UserResource userResource = mock(UserResource.class);
        when(usersResource.get(userId)).thenReturn(userResource);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> keycloackUserService.updateUsername(userId, invalidUsername));
        assertEquals("O e-mail fornecido é inválido.", exception.getMessage());
    }



}
