package com.supera.enem.service;
import com.supera.enem.controller.DTOS.Student.UpdatePasswordDTO;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;

import com.supera.enem.exception.KeycloakException;
import com.supera.enem.exception.ResourceAlreadyExists;
import lombok.extern.slf4j.Slf4j;
import jakarta.ws.rs.core.Response;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;
import java.util.*;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KeycloackUserService {

    @Value("${keycloak.realm-server}")
    private String realm;
    private final Keycloak keycloak;

    public KeycloackUserService(Keycloak keycloak) {
        this.keycloak = keycloak;

    }

    public String createUser(UseKeycloakRegistrationDTO userRegistrationRecord) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(userRegistrationRecord.getUsername());
        user.setEmail(userRegistrationRecord.getEmail());
        user.setFirstName(userRegistrationRecord.getFirstName());
        user.setLastName(userRegistrationRecord.getLastName());
        user.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRegistrationRecord.getPassword());
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        List<CredentialRepresentation> list = new ArrayList<>();
        list.add(credentialRepresentation);
        user.setCredentials(list);
        UsersResource usersResource = getUsersResource();
        Response response = usersResource.create(user);

        if(Objects.equals(201,response.getStatus())){
            List<UserRepresentation> representationList = usersResource.searchByUsername(userRegistrationRecord.getUsername(), true);
            if(!CollectionUtils.isEmpty(representationList)){
                UserRepresentation userRepresentation1 = representationList.stream().filter(userRepresentation -> Objects.equals(false, userRepresentation.isEmailVerified())).findFirst().orElse(null);
                System.out.println("ID: " + userRepresentation1.getId());
                return userRepresentation1.getId();
            }

        }else{
            String errorMessage = response.readEntity(String.class);
            throw new ResourceAlreadyExists("Erro ao criar usuário no Keycloak: " + errorMessage);
        }

        throw new ResourceAlreadyExists("Erro desconhecido ao criar usuário no Keycloak.");
    }


    public void updateEmail(String userId, String newEmail) {
        UsersResource usersResource = getUsersResource();

        UserResource userResource = usersResource.get(userId);

        if (userResource == null) {
            throw new IllegalArgumentException("Usuário não encontrado no keycloak.");
        }
        UserRepresentation userRepresentation = userResource.toRepresentation();


        if (newEmail != null && !newEmail.isEmpty()) {
            userRepresentation.setEmail(newEmail);
            userRepresentation.setEmailVerified(false);
            userResource.update(userRepresentation);

        } else {
            throw new IllegalArgumentException("O e-mail fornecido é inválido.");
        }
    }

    public void updateUsername(String userId, String username) {
        UsersResource usersResource = getUsersResource();
        UserResource userResource = usersResource.get(userId);

        if (userResource == null) {
            throw new IllegalArgumentException("Usuário não encontrado no keycloak.");
        }
        UserRepresentation userRepresentation = userResource.toRepresentation();

        if (username != null && !username.isEmpty()) {
            userRepresentation.setUsername(username);
            userResource.update(userRepresentation);
        } else {
            throw new IllegalArgumentException("O e-mail fornecido é inválido.");
        }
    }

    public String getKeycloakIdByToken(String token) {
        try {
            AccessToken accessToken = TokenVerifier.create(token.replace("Bearer ", ""), AccessToken.class).getToken();
            return accessToken.getSubject();

        } catch (Exception e) {
            throw new IllegalArgumentException("Token inválido.", e);
        }
    }

    public void updatePassword(String userId, UpdatePasswordDTO passwordDTO) {
        UsersResource usersResource = getUsersResource();
        UserResource userResource = usersResource.get(userId);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(passwordDTO.getNewPassword());
        credentialRepresentation.setTemporary(false);

        userResource.resetPassword(credentialRepresentation);
    }

    private UsersResource getUsersResource() {
        RealmResource realmResource = keycloak.realm(realm);
        return realmResource.users();
    }

}
