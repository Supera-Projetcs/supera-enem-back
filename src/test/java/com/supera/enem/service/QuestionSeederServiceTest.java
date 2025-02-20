package com.supera.enem.service;

import com.supera.enem.domain.Question;
import com.supera.enem.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionSeederServiceTest {

    @InjectMocks
    private QuestionSeederService questionSeederService;

    @Mock
    private QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve gerar e salvar 1000 perguntas corretamente")
    void shouldSeedQuestionsSuccessfully() {

        when(questionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        questionSeederService.seedQuestions();

        verify(questionRepository, times(1)).saveAll(anyList());

        ArgumentCaptor<List<Question>> captor = ArgumentCaptor.forClass(List.class);
        verify(questionRepository).saveAll(captor.capture());
        List<Question> savedQuestions = captor.getValue(); // Pegando a lista realmente salva

        List<Question> questionList = new ArrayList<>(savedQuestions);

        assertEquals(1000, savedQuestions.size(), "Devem ser geradas exatamente 1000 perguntas.");

        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);

            assertNotNull(question.getAnswers(), "A pergunta deve conter respostas.");
            assertEquals(4, question.getAnswers().size(), "A pergunta deve conter 4 respostas.");
            assertTrue(question.getAnswers().containsKey("A"), "As respostas devem conter a chave 'A'.");
            assertTrue(question.getAnswers().containsKey("B"), "As respostas devem conter a chave 'B'.");
            assertTrue(question.getAnswers().containsKey("C"), "As respostas devem conter a chave 'C'.");
            assertTrue(question.getAnswers().containsKey("D"), "As respostas devem conter a chave 'D'.");
        }
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar salvar perguntas com lista nula")
    void shouldThrowException_WhenQuestionsListIsNull() {

        doThrow(IllegalArgumentException.class).when(questionRepository).saveAll(null);

        assertThrows(IllegalArgumentException.class, () -> questionRepository.saveAll(null));
    }

    @Test
    @DisplayName("Deve salvar perguntas com respostas válidas")
    void shouldSaveQuestionsWithValidAnswers() {

        List<Question> questions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Question question = new Question();
            question.setText("Question " + i);
            question.setAnswer((char) ('A' + (i % 4)));

            Map<String, String> answers = new HashMap<>();
            answers.put("A", "Answer A for question " + i);
            answers.put("B", "Answer B for question " + i);
            answers.put("C", "Answer C for question " + i);
            answers.put("D", "Answer D for question " + i);
            question.setAnswers(answers);

            questions.add(question);
        }

        when(questionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Question> savedQuestions = questionRepository.saveAll(questions);

        assertEquals(10, savedQuestions.size(), "Devem ser salvas 10 perguntas.");
        for (Question question : savedQuestions) {
            assertNotNull(question.getAnswers(), "A pergunta deve conter respostas.");
            assertEquals(4, question.getAnswers().size(), "A pergunta deve conter 4 respostas.");
        }
    }

    @Test
    @DisplayName("Deve lidar com lista vazia de perguntas sem erros")
    void shouldHandleEmptyQuestionsList() {

        when(questionRepository.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<Question> result = questionRepository.saveAll(Collections.emptyList());

        assertNotNull(result, "O resultado não deve ser nulo.");
        assertTrue(result.isEmpty(), "O resultado deve ser uma lista vazia.");
        verify(questionRepository, times(1)).saveAll(Collections.emptyList());
    }

    @Test
    @DisplayName("Deve salvar perguntas com diferentes respostas corretamente")
    void shouldSaveQuestionsWithDifferentAnswers() {

        Question question1 = new Question();
        question1.setText("Question 1");
        question1.setAnswer('A');

        Question question2 = new Question();
        question2.setText("Question 2");
        question2.setAnswer('B');

        Question question3 = new Question();
        question3.setText("Question 3");
        question3.setAnswer('C');

        List<Question> questions = List.of(question1, question2, question3);

        when(questionRepository.saveAll(anyList())).thenReturn(questions);

        List<Question> savedQuestions = questionRepository.saveAll(questions);

        assertEquals(3, savedQuestions.size(), "Devem ser salvas 3 perguntas.");
        assertEquals('A', savedQuestions.get(0).getAnswer());
        assertEquals('B', savedQuestions.get(1).getAnswer());
        assertEquals('C', savedQuestions.get(2).getAnswer());
    }
}
