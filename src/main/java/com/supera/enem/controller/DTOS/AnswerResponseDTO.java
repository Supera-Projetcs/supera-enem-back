package com.supera.enem.controller.DTOS;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AnswerResponseDTO {
    @Schema(description = "Answer id", example = "1")
    private Long id;
    @Schema(description = "Answer text", example = "A")
    private char text;
    @Schema(description = "Answer correct", example = "true")
    private boolean correct;
    @Schema(description = "Question id", example = "1")
    private Long questionId;
    @Schema(description = "Test id", example = "1")
    private Long testId;
}
