package com.supera.enem.controller.DTOS;

import com.supera.enem.controller.DTOS.Student.StudentDTO;
import com.supera.enem.domain.enums.TestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResponseDTO {
    private Long id;
    private TestType type;
    private Date date;

    private StudentDTO student;
    private List<QuestionResponseDTO> questions;
}
