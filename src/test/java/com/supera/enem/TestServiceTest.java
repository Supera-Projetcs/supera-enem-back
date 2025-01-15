package com.supera.enem;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.mapper.TestMapper;
import com.supera.enem.repository.*;
import com.supera.enem.service.TestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServiceTest {

    @InjectMocks
    private TestService testService;

    @Mock
    private TestRepository testRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TestMapper testMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private void mockAuthentication() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn("keycloakId");
    }

    private Student mockStudent() {
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Estudante teste");
        when(studentRepository.findByKeycloakId("keycloakId")).thenReturn(student);
        return student;
    }

    @Test
    void shouldGenerateTestSuccessfully() {
        mockAuthentication();
        Student student = mockStudent();

        WeeklyReport weeklyReport = new WeeklyReport();
        Content content = new Content();
        weeklyReport.setContents(new HashSet<>(Collections.singletonList(content)));
        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId()))
                .thenReturn(Optional.of(weeklyReport));

        Question question = new Question();
        question.setText("Pergunta exemplo");
        when(questionRepository.findByContents(content))
                .thenReturn(Collections.singletonList(question));

        TestResponseDTO testResponseMock = new TestResponseDTO();
        testResponseMock.setType(TestType.WEEKLY);
        when(testMapper.toDTO(any(TestEntity.class))).thenReturn(testResponseMock);
        when(testRepository.save(any(TestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var testResponse = testService.generateTest();

        assertNotNull(testResponse, "A resposta do teste não deve ser nula.");
        assertEquals(TestType.WEEKLY, testResponse.getType(), "O tipo do teste deve ser SEMANAL.");
        verify(testRepository, times(1)).save(any(TestEntity.class));
    }

    //usuário não autenticado
    @Test
    void shouldThrowExceptionWhenUserNotAuthenticated() {

        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("User not authenticated", exception.getMessage());
    }

    //testes completados
    @Test
    void shouldGetCompletedTests() {

        TestEntity completedTest = new TestEntity();
        completedTest.setType(TestType.WEEKLY);

        when(testRepository.findCompletedTests()).thenReturn(Collections.singletonList(completedTest));
        TestResponseDTO testResponseMock = new TestResponseDTO();
        testResponseMock.setType(TestType.WEEKLY);
        when(testMapper.toDTO(any(TestEntity.class))).thenReturn(testResponseMock);

        var completedTests = testService.getCompletedTests();

        assertNotNull(completedTests, "Os testes completos não devem ser nulos.");
        assertEquals(1, completedTests.size(), "Deve haver um teste completo.");
        verify(testRepository, times(1)).findCompletedTests();
    }

    //id existente do questionário
    @Test
    void shouldGetTestByIdWhenExists() {

        Long testId = 1L;

        TestEntity testEntity = new TestEntity();
        testEntity.setId(testId);

        when(testRepository.findById(testId)).thenReturn(Optional.of(testEntity));
        TestResponseDTO testResponseMock = new TestResponseDTO();
        testResponseMock.setId(testId);

        when(testMapper.toDTO(testEntity)).thenReturn(testResponseMock);

        var testResponse = testService.getTestById(testId);

        assertNotNull(testResponse, "A resposta do teste não deve ser nula.");
        assertEquals(testId, testResponse.getId(), "O ID do teste deve corresponder a um teste que exista.");

        verify(testRepository, times(1)).findById(testId);

        verify(testMapper, times(1)).toDTO(testEntity);
    }

    @Test
    void shouldThrowExceptionWhenTestByIdDoesNotExist() {

        Long testId = 1L;
        when(testRepository.findById(testId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.getTestById(testId));
        assertEquals("Test not found with id: " + testId, exception.getMessage());
        verify(testRepository, times(1)).findById(testId);
    }

    //relatório semanal não é encontrado
    @Test
    void shouldThrowExceptionWhenWeeklyReportNotFound() {

        mockAuthentication();
        Student student = mockStudent();

        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("Weekly report not found", exception.getMessage());
    }
}
