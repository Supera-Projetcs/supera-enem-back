package com.supera.enem.controller.DTOS;

import com.supera.enem.controller.DTOS.Student.StudentResponseDTO;
import com.supera.enem.domain.enums.TestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResponseDTO {
    private Long id;
    private TestType type;
    private Date date;

    private StudentResponseDTO student;
    private List<QuestionResponseDTO> questions;

    private int totalQuestions; // Novo campo
    private int totalCorrectAnswers;

}
