package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;

import com.supera.enem.service.AuthenticationService;
import com.supera.enem.service.WeeklyReportService;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weekly-reports")
public class WeeklyReportController {
    @Autowired
    private WeeklyReportService weeklyReportService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping()
    public ResponseEntity<List<WeeklyReportDTO>> getAllWeeklyReportsByStudent() {
            Student student = authenticationService.getAuthenticatedStudent();
            return ResponseEntity.ok(weeklyReportService.getWeeklyReportsByStudent(student));
    }

    @GetMapping("/{id}")
    public WeeklyReportDTO getWeeklyReportById(@PathVariable Long id, @AuthenticationPrincipal Student student) {

        return weeklyReportService.getWeeklyReportById(id, student);
    }

    @GetMapping("/week")
    public ResponseEntity<WeeklyReport> getWeeklyReport() {
        Student student = authenticationService.getAuthenticatedStudent();
        return ResponseEntity.ok(weeklyReportService.getWeeklyReport(student.getId()));
    }

    @PostMapping("update/{id}")
    public WeeklyReportDTO updateWeeklyReport(@RequestBody WeeklyReportRequestDTO weeklyReportDTO, @PathVariable Long id) {
        return weeklyReportService.updateWeeklyReport(weeklyReportDTO, id);
    }



}
