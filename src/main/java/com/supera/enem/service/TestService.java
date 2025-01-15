package com.supera.enem.service;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.mapper.TestMapper;
import com.supera.enem.repository.QuestionRepository;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.TestRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
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
    @Autowired
    private QuestionRepository questionRepository;

    public List<TestResponseDTO> getCompletedTests() {
        return testRepository.findCompletedTests().stream()
                .map(testMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TestResponseDTO getTestById(Long id) {
        TestEntity testEntity = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));
        return testMapper.toDTO(testEntity);
    }

    public TestResponseDTO generateTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new RuntimeException("User not authenticated");
        }
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getClaim("sub");
        Student student = studentRepository.findByKeycloakId(keycloakId);

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

        for (Content content : contents) {
            List<Question> questions = questionRepository.findByContents(content);
            List<Question> randomQuestions = getRandomQuestions(questions, 10);
            testEntity.getQuestions().addAll(randomQuestions);
        }

        testRepository.save(testEntity);
        return testMapper.toDTO(testEntity);
    }

    public WeeklyReport getLastWeeklyReportByStudent(Student student) {
        return weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId())
                .orElseThrow(() -> new RuntimeException("Weekly report not found"));
    }

    private List<Question> getRandomQuestions(List<Question> questions, int count) {
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

    private boolean hasTestInCurrentWeek(Student student) {
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
