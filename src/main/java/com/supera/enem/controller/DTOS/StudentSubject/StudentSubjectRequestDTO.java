package com.supera.enem.controller.DTOS.StudentSubject;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Setter
@Getter
public class StudentSubjectRequestDTO {
    @NotNull(message = "Apelido não pode ser nulo.")
    private Long subjectId;
    @NotNull(message = "Apelido não pode ser nulo.")
    private Double subjectWeight;
}
