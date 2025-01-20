package com.supera.enem.controller.DTOS;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Student;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeeklyReportResponseDTO {
    private Long id;

    private Date date;

    private Student student;

    private Set<Content> contents;
}
