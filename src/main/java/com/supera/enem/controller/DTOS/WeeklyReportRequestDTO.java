package com.supera.enem.controller.DTOS;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Student;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WeeklyReportRequestDTO {
    private Date date;
    private List<Long> contentIds;
}
