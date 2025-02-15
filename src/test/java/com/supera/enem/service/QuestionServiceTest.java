package com.supera.enem.service;

import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.domain.Question;
import com.supera.enem.mapper.QuestionMapper;
import com.supera.enem.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuestionServiceTest {

    @InjectMocks
    private QuestionService questionService;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionMapper questionMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve retornar todas as questões com sucesso.")
    void shouldReturnAllQuestionsSuccessfully() {
        Question question = new Question();
        question.setId(1L);
        question.setText("Qual é a capital do Rio Grande do Norte?");
        question.setAnswer('A');
        question.setImages(Collections.emptyList());

        when(questionRepository.findAll()).thenReturn(List.of(question));
        when(questionMapper.toDTO(question)).thenReturn(
                new QuestionResponseDTO(1L, "Qual é a capital do Rio Grande do Norte?", 'A', Collections.emptyList(), null)
        );

        List<QuestionResponseDTO> questions = questionService.getAllQuestions();

        assertNotNull(questions);
        assertEquals(1, questions.size());
        assertEquals(1L, questions.get(0).getId());
        assertEquals("Qual é a capital do Rio Grande do Norte?", questions.get(0).getText());
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver questões disponíveis.")
    void shouldReturnEmptyListWhenNoQuestionsAvailable() {
        when(questionRepository.findAll()).thenReturn(Collections.emptyList());

        List<QuestionResponseDTO> questions = questionService.getAllQuestions();

        assertNotNull(questions);
        assertTrue(questions.isEmpty(), "List should be empty when no questions are available.");
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma questão por ID com sucesso.")
    void shouldReturnQuestionByIdSuccessfully() {
        Question question = new Question();
        question.setId(1L);
        question.setText("Qual é a capital da Paraíba?");
        question.setAnswer('B');
        question.setImages(Collections.emptyList());

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionMapper.toDTO(question)).thenReturn(
                new QuestionResponseDTO(1L, "Qual é a capital da Paraíba?", 'B', Collections.emptyList(), null)
        );

        QuestionResponseDTO questionDTO = questionService.getQuestionById(1L);

        assertNotNull(questionDTO);
        assertEquals(1L, questionDTO.getId());
        assertEquals("Qual é a capital da Paraíba?", questionDTO.getText());
        verify(questionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar buscar questão inexistente por ID")
    void shouldThrowExceptionWhenQuestionNotFoundById() {
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> questionService.getQuestionById(999L));

        assertEquals("Question not found", exception.getMessage());
        verify(questionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve salvar uma questão com sucesso")
    void shouldSaveQuestionSuccessfully() {
        Question question = new Question();
        question.setId(1L);
        question.setText("Qual é a capital de Pernambuco?");
        question.setAnswer('C');

        when(questionRepository.save(question)).thenReturn(question);

        Question savedQuestion = questionService.save(question);

        assertNotNull(savedQuestion);
        assertEquals(1L, savedQuestion.getId());
        assertEquals("Qual é a capital de Pernambuco?", savedQuestion.getText());
        verify(questionRepository, times(1)).save(question);
    }

    @Test
    @DisplayName("Deve encontrar uma questão por ID com sucesso")
    void shouldFindQuestionByIdSuccessfully() {
        Question question = new Question();
        question.setId(1L);
        question.setText("Qual é a capital do Ceará?");

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));

        Optional<Question> result = questionService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(questionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio ao buscar uma questão inexistente")
    void shouldReturnEmptyOptionalWhenQuestionNotFoundById() {
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Question> result = questionService.findById(999L);

        assertTrue(result.isEmpty(), "Optional should be empty when question is not found.");
        verify(questionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve lidar com valores extremos de ID (Long.MAX_VALUE)")
    void shouldHandleExtremeLargeIdValues() {
        when(questionRepository.findById(Long.MAX_VALUE)).thenReturn(Optional.empty());

        Optional<Question> result = questionService.findById(Long.MAX_VALUE);

        assertTrue(result.isEmpty(), "Optional should be empty when question ID is Long.MAX_VALUE.");
        verify(questionRepository, times(1)).findById(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("Deve lidar com questões com campos nulos")
    void shouldHandleQuestionWithNullFields() {
        Question question = new Question();
        question.setId(1L);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionMapper.toDTO(question)).thenReturn(
                new QuestionResponseDTO(1L, null, ' ', Collections.emptyList(), null)
        );

        QuestionResponseDTO questionDTO = questionService.getQuestionById(1L);

        assertNotNull(questionDTO);
        assertEquals(1L, questionDTO.getId());
        assertNull(questionDTO.getText(), "Question text should be null.");
        verify(questionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lidar com questões com campos de texto muito grandes")
    void shouldHandleQuestionWithLargeTextFields() {
        String longText = "A".repeat(10_000);
        Question question = new Question();
        question.setId(1L);
        question.setText(longText);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(questionMapper.toDTO(question)).thenReturn(
                new QuestionResponseDTO(1L, longText, 'A', Collections.emptyList(), null)
        );

        QuestionResponseDTO questionDTO = questionService.getQuestionById(1L);

        assertNotNull(questionDTO);
        assertEquals(1L, questionDTO.getId());
        assertEquals(longText, questionDTO.getText(), "Question text should match the long text.");
        verify(questionRepository, times(1)).findById(1L);
    }
}
