package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.service.WeeklyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weekly-reports")
public class WeeklyReportController {
    @Autowired
    private WeeklyReportService weeklyReportService;

    @GetMapping
    public List<WeeklyReportDTO> getAllWeeklyReportsByStudent(@AuthenticationPrincipal Student student) {
        List<WeeklyReport> weeklyReports = weeklyReportService.getWeeklyReportsByStudent(student);

        return weeklyReports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public WeeklyReportDTO getWeeklyReportById(@PathVariable Long id, @AuthenticationPrincipal Student student) {
        WeeklyReport weeklyReport = weeklyReportService.getWeeklyReportById(id, student);
        if (weeklyReport == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Weekly report not found");
        }
        return convertToDTO(weeklyReport);
    }

    private WeeklyReportDTO convertToDTO(WeeklyReport weeklyReport) {

        WeeklyReportDTO dto = new WeeklyReportDTO();
        dto.setId(weeklyReport.getId());
        dto.setStudentId(weeklyReport.getStudent().getId());
        dto.setDate(weeklyReport.getDate());

        return dto;
    }
}
