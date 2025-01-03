package com.supera.enem.mapper;


import com.supera.enem.dto.StudentRegistrationRecord;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {


    public UserRepresentation toUserRepresentation(StudentRegistrationRecord studentRegistrationRecord) {
        UserRepresentation userRepresentation = new UserRepresentation();

        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(studentRegistrationRecord.username());
        userRepresentation.setEmail(studentRegistrationRecord.email());
        userRepresentation.setFirstName(studentRegistrationRecord.firstName());
        userRepresentation.setLastName(studentRegistrationRecord.lastName());
        userRepresentation.setEmailVerified(false);

        return userRepresentation;
    }

    public StudentRegistrationRecord toStudentRegistrationRecord(UserRepresentation userRepresentation) {
        return new StudentRegistrationRecord(
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                userRepresentation.getFirstName(),
                userRepresentation.getLastName(),
                ""
        );
    }
}
