package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import com.supera.enem.service.WeeklyReportService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public List<WeeklyReportDTO> getAllWeeklyReportsByStudent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String keycloakId = jwt.getClaim("sub");
            Student student = studentRepository.findByKeycloakId(keycloakId);
            List<WeeklyReport> weeklyReports = weeklyReportService.getWeeklyReportsByStudent(student);

            return weeklyReports.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
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
        dto.setContents(weeklyReport.getContents());
        return dto;
    }
}
