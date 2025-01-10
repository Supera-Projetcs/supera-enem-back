package com.supera.enem.controller.DTOS;

import com.supera.enem.domain.Content;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class WeeklyReportDTO {
    private Long id;
    private Date date;
    private Long studentId;
    private Set<Content> contents;
}
