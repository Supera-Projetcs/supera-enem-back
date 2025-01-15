package com.supera.enem.controller.DTOS;

import com.supera.enem.domain.enums.TestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestResponseDTO {
    private Long id;
    private TestType type;
    private Date date;
}
