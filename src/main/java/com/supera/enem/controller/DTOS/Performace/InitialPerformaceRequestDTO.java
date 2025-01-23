package com.supera.enem.controller.DTOS.Performace;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class InitialPerformaceRequestDTO {

    @NotNull(message = "O desempenho em Física não pode ser nulo")
    private Double performaceValue;

    @NotNull(message = "O desempenho em Química não pode ser nulo")
    private Long subjectId;
}
