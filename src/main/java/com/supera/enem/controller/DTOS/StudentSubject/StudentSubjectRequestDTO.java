package com.supera.enem.controller.DTOS.StudentSubject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Data
@Setter
@Getter
public class StudentSubjectRequestDTO {
    @Schema(description = "ID da matéria", required = true)
    @NotNull(message = "Id não pode ser nulo.")
    private Long subjectId;
    private Double subjectWeight;
}
