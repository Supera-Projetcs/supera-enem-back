package com.supera.enem.controller.DTOS.Student;

import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.domain.enums.Weekday;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;


@Getter
@Setter
public class StudentDTO {

    @NotNull(message = "Apelido não pode ser nulo.")
    private String username;
    @NotNull(message = "Nome não pode ser nulo.")
    private String firstName;
    @NotNull(message = "Sobrenome não pode ser nulo.")
    private String lastName;
    @NotNull(message = "Curso dos sonho não pode ser nulo.")
    private String dreamCourse;
    @NotNull(message = "Telefone dos sonho não pode ser nulo.")
    private String phone;

    @NotNull(message = "Email não pode ser nulo.")
    private String email;

    @NotNull(message = "Aniversário não pode ser nulo.")
    private LocalDate birthDate;
    @NotNull(message = "Endereço não pode ser nulo.")
    private AddressDTO address;
    @NotNull(message = "Senha não pode ser nulo.")
    private String password;
    @NotNull(message = "Dias de estudo não pode ser nulo.")
    private Set<Weekday> preferredStudyDays;

}
