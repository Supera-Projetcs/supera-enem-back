package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.Performace.InitialPerformaceRequestDTO;
import com.supera.enem.controller.DTOS.Performace.PerformaceResponseDTO;
import com.supera.enem.controller.DTOS.SubjectDifficultyDTO;
import com.supera.enem.domain.Performance;
import com.supera.enem.service.PerformanceService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/initial-performace/{studentId}")
    public ResponseEntity<List<PerformaceResponseDTO>> createInitialPerformance(@PathVariable Long studentId, @RequestBody List<InitialPerformaceRequestDTO> dto) {
        return ResponseEntity.ok(performanceService.createInitialPerformance(studentId, dto));
    }
}
