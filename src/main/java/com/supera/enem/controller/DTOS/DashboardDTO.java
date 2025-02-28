package com.supera.enem.controller.DTOS;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DashboardDTO {
    private long numberOfRightAnswers;
    private long numberOfWrongAnswers;
    private long numberOfTests;
    private double hitRate;
}
