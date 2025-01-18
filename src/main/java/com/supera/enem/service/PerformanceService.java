package com.supera.enem.service;

import com.supera.enem.controller.DTOS.SubjectDifficultyDTO;
import com.supera.enem.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    public List<SubjectDifficultyDTO> getSubjectDifficulties(Long studentId) {

        if (studentId == null) {
            throw new IllegalArgumentException("Student ID must not be null");
        }

        List<Object[]> results = performanceRepository.findAveragePerformanceBySubject(studentId);

        return results.stream().map(result -> {
            String subjectName = (String) result[0];
            double avgPerformance = ((Number) result[1]).doubleValue();

            String difficulty;
            if (avgPerformance > 70) {
                difficulty = "EASY";
            } else if (avgPerformance >= 40) {
                difficulty = "MEDIUM";
            } else {
                difficulty = "HARD";
            }

            return new SubjectDifficultyDTO(subjectName, difficulty);
        }).collect(Collectors.toList());
    }
}
