package com.supera.enem.service;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.controller.DTOS.AnswerResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.exception.ResourceAlreadyExists;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.AnswerMapper;
import com.supera.enem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@DisplayName("Testes para o serviço de respostas.")
public class AnswerServiceTest {

    @InjectMocks
    private AnswerService answerService;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private TestRepository testRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private AnswerMapper answerMapper;

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
        student.setKeycloakId("keycloakId");
        student.setId(1L);
        return student;
    }

    @Test
    @DisplayName("Deve criar uma resposta com sucesso.")
    void shouldCreateAnswerSuccessfully() {
        mockAuthentication();
        Student student = mockStudent();

        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', 1L, 1L);

        Question question = new Question();
        question.setId(1L);
        question.setAnswer('A');

        TestEntity test = new TestEntity();
        test.setId(1L);
        test.setStudent(student);

        Answer answer = new Answer();
        answer.setId(1L);
        answer.setCorrect(true);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        when(answerRepository.existsByTestEntityAndQuestion(test, question)).thenReturn(false);
        when(answerMapper.toEntity(requestDTO)).thenReturn(answer);
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);
        when(answerMapper.toDTO(any(Answer.class))).thenReturn(new AnswerResponseDTO(1L, 'A', true, 1L, 1L));

        AnswerResponseDTO response = answerService.createAnswer(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertTrue(response.isCorrect());
        verify(answerRepository, times(1)).save(any(Answer.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar resposta com DTO nulo.")
    void shouldThrowExceptionWhenAnswerRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> answerService.createAnswer(null));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar resposta com campos nulos no DTO.")
    void shouldThrowExceptionWhenAnswerRequestFieldsAreNull() {
        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', null, null);

        assertThrows(IllegalArgumentException.class, () -> answerService.createAnswer(requestDTO));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a questão não for encontrada")
    void shouldThrowExceptionWhenQuestionNotFound() {
        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', 999L, 1L);

        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> answerService.createAnswer(requestDTO));

        assertEquals("Question not found", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o teste não for encontrado")
    void shouldThrowExceptionWhenTestNotFound() {
        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', 1L, 999L);

        Question question = new Question();
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(testRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> answerService.createAnswer(requestDTO));

        assertEquals("Test not found", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando uma resposta já existe.")
    void shouldThrowExceptionWhenAnswerAlreadyExists() {
        mockAuthentication();
        Student student = mockStudent();

        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', 1L, 1L);

        Question question = new Question();
        question.setId(1L);

        TestEntity test = new TestEntity();
        test.setId(1L);
        test.setStudent(student);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        when(answerRepository.existsByTestEntityAndQuestion(test, question)).thenReturn(true);

        ResourceAlreadyExists exception = assertThrows(ResourceAlreadyExists.class,
                () -> answerService.createAnswer(requestDTO));

        assertEquals("An answer already exists for this question in the test", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar respostas do teste com sucesso")
    void shouldReturnAnswersByTestSuccessfully() {
        mockAuthentication();
        Student student = mockStudent();

        TestEntity test = new TestEntity();
        test.setId(1L);
        test.setStudent(student);

        Answer answer = new Answer();
        answer.setId(1L);
        answer.setText('A');
        answer.setCorrect(true);

        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        when(answerRepository.findAll()).thenReturn(List.of(answer));
        when(answerMapper.toDTOList(anyList())).thenReturn(List.of(new AnswerResponseDTO(1L, 'A', true, 1L, 1L)));

        List<AnswerResponseDTO> responses = answerService.getAnswersByTest(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getId());
        verify(answerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar respostas de teste inexistente")
    void shouldThrowExceptionWhenTestNotFoundForAnswers() {
        when(testRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> answerService.getAnswersByTest(999L));

        assertEquals("Test not found", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver respostas para o teste")
    void shouldReturnEmptyListWhenNoAnswersForTest() {
        mockAuthentication();
        Student student = mockStudent();

        TestEntity test = new TestEntity();
        test.setId(1L);
        test.setStudent(student);

        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        when(answerRepository.findAll()).thenReturn(Collections.emptyList());

        List<AnswerResponseDTO> responses = answerService.getAnswersByTest(1L);

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(answerRepository, times(1)).findAll();
    }
}
