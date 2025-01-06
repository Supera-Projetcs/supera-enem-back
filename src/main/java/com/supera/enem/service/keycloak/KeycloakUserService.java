package com.supera.enem.service.keycloak;

import com.supera.enem.controller.DTOS.ResetPasswordDTO;
import com.supera.enem.controller.DTOS.StudentRegistrationRecord;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakUserService {

    String createUser(StudentRegistrationRecord userRegistrationRecord);
    UserRepresentation getUserById(String userId);
    void deleteUserById(String userId);
    void emailVerification(String userId);
    UserResource getUserResource(String userId);
    void updatePassword(String userId);
    void updatePassword(ResetPasswordDTO resetPassword, String userId);


}
