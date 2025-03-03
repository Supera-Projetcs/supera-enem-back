package com.supera.enem.controller.DTOS;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Student;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeeklyReportResponseDTO {
    private Long id;
    private Date date;
    private List<Content> contents;
}
