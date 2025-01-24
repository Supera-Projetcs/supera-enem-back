package com.supera.enem.controller.DTOS;

import com.supera.enem.domain.Subject;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ContentDTO {
    private Long id;
    private String name;
    private double contentWeight;
    private double questionWeight;
    private Subject subject;
}
