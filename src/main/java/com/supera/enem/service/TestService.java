package com.supera.enem.service;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.Content;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.Test;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.mapper.TestMapper;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.TestRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;
    private final TestMapper testMapper = TestMapper.INSTANCE;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private WeeklyReportRepository weeklyReportRepository;

    public List<TestResponseDTO> getCompletedTests() {
        return testRepository.findCompletedTests().stream()
                .map(testMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TestResponseDTO getTestById(Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));
        return testMapper.toDTO(test);
    }

    public TestResponseDTO generateTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getClaim("sub");
        Student student = studentRepository.findByKeycloakId(keycloakId);

        WeeklyReport lastWeeklyReport = getLastWeeklyReportByStudent(student);

        Set<Content> contents = lastWeeklyReport.getContents();

        Test test = new Test();
        test.setStudent(student);
        test.setType(TestType.WEEKLY);
        testRepository.save(test);
        return testMapper.toDTO(test);
    }

    public WeeklyReport getLastWeeklyReportByStudent(Student student) {
        return weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId())
                .orElseThrow(() -> new RuntimeException("Weekly report not found"));
    }
}
