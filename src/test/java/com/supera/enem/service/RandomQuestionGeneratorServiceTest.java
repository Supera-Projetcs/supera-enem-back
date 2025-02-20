package com.supera.enem.service;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Question;
import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RandomQuestionGeneratorServiceTest {

    @InjectMocks
    private RandomQuestionGeneratorService service;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ContentRepository contentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve atribuir perguntas aleatórias a conteúdos com sucesso")
    void shouldAssignRandomQuestionsToContentsSuccessfully() {

        List<Content> mockContents = List.of(
                new Content(1L, "Content 1", 0.5, 0.5, null, new HashSet<>(), new HashSet<>()),
                new Content(2L, "Content 2", 0.6, 0.4, null, new HashSet<>(), new HashSet<>())
        );

        List<Question> mockQuestions = List.of(
                new Question(1L, "Question 1", 'A', new ArrayList<>(), null, null, new HashSet<>()),
                new Question(2L, "Question 2", 'B', new ArrayList<>(), null, null, new HashSet<>()),
                new Question(3L, "Question 3", 'C', new ArrayList<>(), null, null, new HashSet<>())
        );

        when(contentRepository.findAll()).thenReturn(mockContents);
        when(questionRepository.findAll()).thenReturn(mockQuestions);

        service.assignRandomQuestionsToContents();

        for (Content content : mockContents) {
            assertNotNull(content.getQuestions(), "As perguntas não devem ser nulas.");
            assertFalse(content.getQuestions().isEmpty(), "As perguntas devem ser atribuídas.");
            assertTrue(content.getQuestions().size() <= 5, "Cada conteúdo deve ter no máximo 5 perguntas atribuídas.");
        }

        verify(contentRepository, times(mockContents.size())).save(any(Content.class));
    }

    @Test
    @DisplayName("Deve lidar com lista vazia de conteúdos")
    void shouldHandleEmptyContentList() {
        when(contentRepository.findAll()).thenReturn(List.of());
        when(questionRepository.findAll()).thenReturn(List.of(
                new Question(1L, "Question 1", 'A', new ArrayList<>(), null, null, new HashSet<>())
        ));

        service.assignRandomQuestionsToContents();

        verify(contentRepository, never()).save(any(Content.class));
    }

    @Test
    @DisplayName("Deve lidar com lista vazia de perguntas")
    void shouldHandleEmptyQuestionList() {
        List<Content> mockContents = List.of(
                new Content(1L, "Content 1", 0.5, 0.5, null, new HashSet<>(), new HashSet<>())
        );

        when(contentRepository.findAll()).thenReturn(mockContents);
        when(questionRepository.findAll()).thenReturn(List.of());

        service.assignRandomQuestionsToContents();

        for (Content content : mockContents) {
            assertTrue(content.getQuestions().isEmpty(), "As perguntas atribuídas devem ser vazias");
        }

        verify(contentRepository, never()).save(any(Content.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atribuir mais perguntas do que disponíveis")
    void shouldThrowExceptionWhenRequestingMoreQuestionsThanAvailable() {
        List<Content> mockContents = List.of(
                new Content(1L, "Content 1", 0.5, 0.5, null, new HashSet<>(), new HashSet<>())
        );

        List<Question> mockQuestions = List.of( // 👈 Menos de 5 perguntas
                new Question(1L, "Question 1", 'A', new ArrayList<>(), null, null, new HashSet<>()),
                new Question(2L, "Question 2", 'B', new ArrayList<>(), null, null, new HashSet<>())
        );

        when(contentRepository.findAll()).thenReturn(mockContents);
        when(questionRepository.findAll()).thenReturn(mockQuestions);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> service.assignRandomQuestionsToContents());

        assertEquals("Insufficient questions to assign", exception.getMessage());
    }

}
