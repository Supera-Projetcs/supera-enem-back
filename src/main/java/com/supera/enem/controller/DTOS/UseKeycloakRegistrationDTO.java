package com.supera.enem.controller.DTOS;
import lombok.Data;

@Data
public class UseKeycloakRegistrationDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
