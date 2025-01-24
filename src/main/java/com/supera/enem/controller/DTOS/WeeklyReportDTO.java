package com.supera.enem.controller.DTOS;

import com.supera.enem.domain.Content;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Data
@Getter
@Setter
public class WeeklyReportDTO {
    private Long id;
    private Date date;
    private Long studentId;
    private Set<Content> contents;
}
