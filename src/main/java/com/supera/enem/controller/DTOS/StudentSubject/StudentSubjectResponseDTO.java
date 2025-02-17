package com.supera.enem.controller.DTOS.StudentSubject;

import com.supera.enem.controller.DTOS.SubjectDTO;
import lombok.*;


@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StudentSubjectResponseDTO {
    private Long id;
    private Double subjectWeight;
    private SubjectDTO subject;
}
