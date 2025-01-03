package com.supera.enem.service.keycloak;

import com.supera.enem.dto.ResetPasswordDTO;
import com.supera.enem.dto.StudentRegistrationRecord;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;

public interface KeycloakUserService {

    StudentRegistrationRecord createUser(StudentRegistrationRecord userRegistrationRecord);
    UserRepresentation getUserById(String userId);
    void deleteUserById(String userId);
    void emailVerification(String userId);
    UserResource getUserResource(String userId);
    void updatePassword(String userId);
    void updatePassword(ResetPasswordDTO resetPassword, String userId);


}
