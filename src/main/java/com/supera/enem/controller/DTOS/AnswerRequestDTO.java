package com.supera.enem.controller.DTOS;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AnswerRequestDTO {
    private char text;
    private Long questionId;
    private Long testId;
}
