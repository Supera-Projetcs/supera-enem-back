package com.supera.enem.controller.DTOS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UseKeycloakRegistrationDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
