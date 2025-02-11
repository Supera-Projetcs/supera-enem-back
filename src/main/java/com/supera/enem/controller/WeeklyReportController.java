package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;

import com.supera.enem.service.AuthenticatedService;
import com.supera.enem.service.WeeklyReportService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weekly-reports")
public class WeeklyReportController {
    @Autowired
    private WeeklyReportService weeklyReportService;

    @Autowired
    private AuthenticatedService authenticationService;


    @GetMapping("/{id}")
    public WeeklyReportDTO getWeeklyReportById(@PathVariable Long id, @AuthenticationPrincipal Student student) {

        return weeklyReportService.getWeeklyReportById(id, student);
    }

    @GetMapping("/week")
    public ResponseEntity<WeeklyReportDTO> getWeeklyReport() {
        return ResponseEntity.ok(weeklyReportService.getWeeklyReport());
    }

    @PostMapping("update/{id}")
    public WeeklyReportDTO updateWeeklyReport(@RequestBody WeeklyReportRequestDTO weeklyReportDTO, @PathVariable Long id) {
        return weeklyReportService.updateWeeklyReport(weeklyReportDTO, id);
    }



}
