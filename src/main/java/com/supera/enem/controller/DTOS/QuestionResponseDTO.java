package com.supera.enem.controller.DTOS;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class QuestionResponseDTO {
    private Long id;
    private String text;
    private char answer;
    private List<String> images;
    private Map<String, String> answers;
}