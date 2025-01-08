package com.supera.enem.controller.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDifficultyDTO {
    private String subjectName;
    private String difficulty;
}
