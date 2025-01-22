package com.supera.enem.service;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.TestMapper;
import com.supera.enem.repository.QuestionRepository;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.TestRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TestService {
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private TestMapper testMapper;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private WeeklyReportRepository weeklyReportRepository;
    @Autowired
    private QuestionRepository questionRepository;

    public List<TestResponseDTO> getCompletedTests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        return testRepository.findCompletedTests().stream()
                .map(testMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TestResponseDTO getTestById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }

        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
        if (id < 0) {
            throw new IllegalArgumentException("Invalid ID: " + id);
        }

        TestEntity testEntity = testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found with id: " + id));
        return testMapper.toDTO(testEntity);
    }

    @Transactional
    public TestResponseDTO generateTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getClaim("sub");

        //validação para a ausencia do sub no jwt
        if (keycloakId == null) {
            throw new RuntimeException("User not authenticated");
        }


        Student student = studentRepository.findByKeycloakId(keycloakId);

        if (student == null) {
            throw new RuntimeException("Student not found");
        }

        if (hasTestInCurrentWeek(student)) {
            throw new RuntimeException("Test for the current week already exists.");
        }

        WeeklyReport lastWeeklyReport = getLastWeeklyReportByStudent(student);
        if (lastWeeklyReport == null) {
            throw new RuntimeException("No weekly report found for the student");
        }

        Set<Content> contents = lastWeeklyReport.getContents();
        if (contents.isEmpty()) {
            throw new RuntimeException("No content found in the weekly report");
        }

        TestEntity testEntity = new TestEntity();
        testEntity.setStudent(student);
        testEntity.setType(TestType.WEEKLY);

        Set<Question> uniqueQuestions = new HashSet<>();

        for (Content content : contents) {
            List<Question> questions = questionRepository.findRandomQuestionsByContent(content.getId(), 10);

            for (Question question : questions) {
                if (uniqueQuestions.add(question)) {
                    testEntity.getQuestions().add(question);
                }
            }
        }

        testRepository.save(testEntity);
        return testMapper.toDTO(testEntity);
    }

    public WeeklyReport getLastWeeklyReportByStudent(Student student) {
        return weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId())
                .orElseThrow(() -> new RuntimeException("Weekly report not found"));
    }

    public List<Question> getRandomQuestions(List<Question> questions, int count) {
        if (questions.size() <= count) {
            return questions; // Return all questions if less than or equal to count
        }
        Random random = new Random();
        return random.ints(0, questions.size())
                .distinct()
                .limit(count)
                .mapToObj(questions::get)
                .toList();
    }

    public boolean hasTestInCurrentWeek(Student student) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

        List<TestEntity> testEntities = testRepository.findByStudentAndDateBetween(
                student,
                java.sql.Date.valueOf(startOfWeek),
                java.sql.Date.valueOf(endOfWeek)
        );

        return !testEntities.isEmpty();
    }
}
