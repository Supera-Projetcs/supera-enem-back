package com.supera.enem;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.controller.DTOS.AnswerResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.exception.ResourceAlreadyExists;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.AnswerMapper;
import com.supera.enem.repository.*;
import com.supera.enem.service.AnswerService;
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
    void shouldCreateAnswerSuccessfully() {
        mockAuthentication();
        Student student = mockStudent();

        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', 1L, 1L);

        Question question = new Question();
        question.setId(1L);
        question.setAnswer('A');
        question.setContents(Set.of(new Content()));

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
    void shouldThrowExceptionWhenQuestionNotFound() {

        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', 999L, 1L);

        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> answerService.createAnswer(requestDTO));

        assertEquals("Question not found", exception.getMessage());
    }

    @Test
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
    void shouldThrowExceptionWhenTestNotFoundForAnswers() {

        when(testRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> answerService.getAnswersByTest(999L));

        assertEquals("Test not found", exception.getMessage());
    }
}
