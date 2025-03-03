package com.supera.enem.service;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.exception.ResourceAlreadyExists;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.TestMapper;
import com.supera.enem.repository.QuestionRepository;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.TestRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
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
    @Autowired
    private AuthenticatedService authenticatedService;

    @Transactional()
    public Page<TestResponseDTO> getCompletedTests(int page, int size) {
        Student student = authenticatedService.getAuthenticatedStudent();
        if (student == null) {
            throw new RuntimeException("User not authenticated");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<TestEntity> testEntities = testRepository.findCompletedTests(pageable);
        return testEntities.map(testMapper::toDTO);
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

    @Transactional()
    public boolean wasThisWeekTestCompleted() {
        List<TestEntity> testEntities = getThisWeekTests();
        if (testEntities.isEmpty()) {
            return false;
        }
        TestEntity lastTest = testEntities.get(0);
        return lastTest.areAllQuestionsAnswered();
    }

    @Transactional
    public TestResponseDTO generateTest() {
        Student student = authenticatedService.getAuthenticatedStudent();

        if (hasTestInCurrentWeek()) {
            throw new ResourceAlreadyExists("Test for the current week already exists.");
        }

        WeeklyReport lastWeeklyReport = getLastWeeklyReportByStudent();
        if (lastWeeklyReport == null) {
            throw new ResourceNotFoundException("No weekly report found for the student");
        }

        Set<Content> contents = lastWeeklyReport.getContents();
        if (contents.isEmpty()) {
            throw new ResourceNotFoundException("No content found in the weekly report");
        }

        TestEntity testEntity = new TestEntity();
        testEntity.setStudent(student);
        testEntity.setType(TestType.WEEKLY);

        Set<Question> uniqueQuestions = new HashSet<>();

        for (Content content : contents) {
            List<Question> questions = questionRepository.findRandomQuestionsByContent(content.getId(), 10);

            if (questions.isEmpty()) {
                throw new ResourceNotFoundException("No questions found for content with id: " + content.getId());
            }

            for (Question question : questions) {
                if (uniqueQuestions.add(question)) {
                    testEntity.getQuestions().add(question);
                }
            }
        }

        testRepository.save(testEntity);
        return testMapper.toDTO(testEntity);
    }

    @Transactional()
    public WeeklyReport getLastWeeklyReportByStudent() {
        Student student = authenticatedService.getAuthenticatedStudent();
        return weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId())
                .orElseThrow(() -> new RuntimeException("Weekly report not found"));
    }

    public List<Question> getRandomQuestions(List<Question> questions, int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }

        List<Question> uniqueQuestions = new ArrayList<>(new HashSet<>(questions));

        if (uniqueQuestions.size() <= count) {
            return uniqueQuestions;
        }

        Random random = new Random();
        return random.ints(0, questions.size())
                .distinct()
                .limit(count)
                .mapToObj(questions::get)
                .toList();
    }

    public boolean hasTestInCurrentWeek() {
        List<TestEntity> testEntities = getThisWeekTests();
        System.out.println("puta testes: " + testEntities);
        return !testEntities.isEmpty();
    }

    public  List<TestEntity> getThisWeekTests() {
        Student student = authenticatedService.getAuthenticatedStudent();
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

        return testRepository.findByStudentAndDateBetween(
                student,
                java.sql.Date.valueOf(startOfWeek),
                java.sql.Date.valueOf(endOfWeek)
        );
    }

}
