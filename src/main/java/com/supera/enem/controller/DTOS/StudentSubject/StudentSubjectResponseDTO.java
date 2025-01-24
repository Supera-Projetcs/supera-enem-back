package com.supera.enem.controller.DTOS.StudentSubject;

import com.supera.enem.controller.DTOS.SubjectDTO;
import com.supera.enem.domain.Subject;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class StudentSubjectResponseDTO {
    private Long id;
    private Double subjectWeight;
    private SubjectDTO subject;
}
