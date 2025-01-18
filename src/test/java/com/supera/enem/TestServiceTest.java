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

    @Test
    void shouldGenerateTestWithMultipleContents() {
        mockAuthentication();
        Student student = mockStudent();

        //múltiplos conteúdos relatório semanal
        WeeklyReport weeklyReport = new WeeklyReport();
        Content content1 = new Content();
        content1.setId(1L);
        Content content2 = new Content();
        content2.setId(2L);
        weeklyReport.setContents(new HashSet<>(Arrays.asList(content1, content2)));

        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId()))
                .thenReturn(Optional.of(weeklyReport));

        //perguntas
        Question question1 = new Question();
        question1.setText("Pergunta 1 do conteúdo 1");
        when(questionRepository.findRandomQuestionsByContent(content1.getId(), 10))
                .thenReturn(Collections.singletonList(question1));

        Question question2 = new Question();
        question2.setText("Pergunta 1 do conteúdo 2");
        when(questionRepository.findRandomQuestionsByContent(content2.getId(), 10))
                .thenReturn(Collections.singletonList(question2));

        when(testMapper.toDTO(any(TestEntity.class))).thenReturn(new TestResponseDTO());
        when(testRepository.save(any(TestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var testResponse = testService.generateTest();

        assertNotNull(testResponse);
        verify(testRepository, times(1)).save(any(TestEntity.class));
        verify(questionRepository, times(1)).findRandomQuestionsByContent(content1.getId(), 10);
        verify(questionRepository, times(1)).findRandomQuestionsByContent(content2.getId(), 10);
    }

    @Test
    void shouldNotIncludeDuplicateQuestions() {
        mockAuthentication();
        Student student = mockStudent();

        WeeklyReport weeklyReport = new WeeklyReport();
        Content content = new Content();
        content.setId(1L);
        weeklyReport.setContents(Set.of(content));

        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId()))
                .thenReturn(Optional.of(weeklyReport));

        //duas perguntas iguais
        Question question1 = new Question();
        question1.setId(1L);
        question1.setText("Pergunta duplicada");
        List<Question> questions = Arrays.asList(question1, question1);

        when(questionRepository.findRandomQuestionsByContent(content.getId(), 10))
                .thenReturn(questions);

        when(testMapper.toDTO(any(TestEntity.class))).thenReturn(new TestResponseDTO());
        when(testRepository.save(any(TestEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var testResponse = testService.generateTest();

        assertNotNull(testResponse);
        verify(testRepository, times(1)).save(any(TestEntity.class));
        verify(questionRepository, times(1)).findRandomQuestionsByContent(content.getId(), 10);
    }

    @Test
    void shouldThrowExceptionWhenWeeklyReportHasNoContents() {
        mockAuthentication();
        Student student = mockStudent();

        WeeklyReport weeklyReport = new WeeklyReport();
        weeklyReport.setContents(Collections.emptySet());

        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId()))
                .thenReturn(Optional.of(weeklyReport));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("No content found in the weekly report", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSubClaimIsMissing() {
        //claim "sub" ausente
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(jwt);
        when(jwt.getClaim("sub")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenTestExistsInCurrentWeek() {
        Student student = mockStudent();

        List<TestEntity> tests = List.of(new TestEntity());
        when(testRepository.findByStudentAndDateBetween(
                eq(student),
                any(),
                any())
        ).thenReturn(tests);

        boolean result = testService.hasTestInCurrentWeek(student);

        assertTrue(result, "true quando houver testes.");
        verify(testRepository, times(1)).findByStudentAndDateBetween(any(), any(), any());
    }

    @Test
    void shouldReturnFalseWhenNoTestExistsInCurrentWeek() {
        Student student = mockStudent();

        when(testRepository.findByStudentAndDateBetween(
                eq(student),
                any(),
                any())
        ).thenReturn(Collections.emptyList());

        boolean result = testService.hasTestInCurrentWeek(student);

        assertFalse(result, "false quando não houver testes.");
        verify(testRepository, times(1)).findByStudentAndDateBetween(any(), any(), any());
    }

    @Test
    void shouldReturnAllQuestionsWhenLessThanOrEqualToLimit() {
        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Question question = new Question();
            question.setId((long) i);
            questions.add(question);
        }

        List<Question> result = testService.getRandomQuestions(questions, 10);

        assertEquals(5, result.size(), "todas as perguntas quando o número for menor ou igual ao limite.");
    }

    @Test
    void shouldReturnLimitedQuestionsWhenMoreThanLimit() {
        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            Question question = new Question();
            question.setId((long) i);
            questions.add(question);
        }

        List<Question> result = testService.getRandomQuestions(questions, 10);

        assertEquals(10, result.size(), "retornar o limite de perguntas quando houver mais disponíveis.");
        assertTrue(new HashSet<>(result).size() == result.size(), "Todas as perguntas devem ser únicas.");
    }
}



