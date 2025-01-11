package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.SubjectDifficultyDTO;
import com.supera.enem.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @GetMapping("/difficulties/{studentId}")
    public List<SubjectDifficultyDTO> getSubjectDifficulties(@PathVariable Long studentId) {
        return performanceService.getSubjectDifficulties(studentId);
    }
}
