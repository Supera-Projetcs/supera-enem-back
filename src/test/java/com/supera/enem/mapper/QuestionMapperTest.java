package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.domain.Image;
import com.supera.enem.domain.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionMapperTest {

    private final QuestionMapper questionMapper = Mappers.getMapper(QuestionMapper.class);

    @Test
    @DisplayName("Deve mapear uma questão válida para QuestionResponseDTO")
    void shouldMapQuestionToQuestionResponseDTO_Successfully() {
        Question question = new Question();
        question.setId(1L);
        question.setText("Qual é a capital do Brasil?");
        question.setAnswer('A');

        Image image = new Image();
        image.setUrl("image1.jpg");
        question.setImages(List.of(image));

        QuestionResponseDTO responseDTO = questionMapper.toDTO(question);

        assertNotNull(responseDTO, "O DTO não deve ser nulo.");
        assertEquals(1L, responseDTO.getId(), "O ID deve ser mapeado corretamente.");
        assertEquals("Qual é a capital do Brasil?", responseDTO.getText(), "O texto deve ser mapeado corretamente.");
        assertEquals('A', responseDTO.getAnswer(), "A resposta deve ser mapeada corretamente.");
        assertEquals(1, responseDTO.getImages().size(), "O número de imagens deve ser mapeado corretamente.");
        assertEquals("image1.jpg", responseDTO.getImages().get(0), "O caminho da imagem deve ser mapeado corretamente.");
    }

    @Test
    @DisplayName("Deve lidar com questões sem imagens ao mapear para QuestionResponseDTO")
    void shouldHandleQuestionWithoutImages_WhenMappingToQuestionResponseDTO() {
        Question question = new Question();
        question.setId(2L);
        question.setText("Qual é a capital da Argentina?");
        question.setAnswer('B');
        question.setImages(null);

        QuestionResponseDTO responseDTO = questionMapper.toDTO(question);

        assertNotNull(responseDTO, "O DTO não deve ser nulo.");
        assertEquals(2L, responseDTO.getId(), "O ID deve ser mapeado corretamente.");
        assertEquals("Qual é a capital da Argentina?", responseDTO.getText(), "O texto deve ser mapeado corretamente.");
        assertEquals('B', responseDTO.getAnswer(), "A resposta deve ser mapeada corretamente.");
        assertTrue(responseDTO.getImages().isEmpty(), "A lista de imagens deve ser vazia.");
    }

    @Test
    @DisplayName("Deve retornar DTO com valores padrão para uma questão com campos nulos")
    void shouldHandleQuestionWithNullFields_WhenMappingToQuestionResponseDTO() {
        Question question = new Question();

        QuestionResponseDTO responseDTO = questionMapper.toDTO(question);

        assertNotNull(responseDTO, "O DTO não deve ser nulo.");
        assertNull(responseDTO.getId(), "O ID deve ser nulo.");
        assertNull(responseDTO.getText(), "O texto deve ser nulo.");
        assertEquals('\0', responseDTO.getAnswer(), "A resposta deve ser o valor padrão.");
        assertTrue(responseDTO.getImages().isEmpty(), "A lista de imagens deve ser vazia.");
    }

    @Test
    @DisplayName("Deve mapear uma lista de questões válidas para uma lista de QuestionResponseDTOs")
    void shouldMapListOfQuestionsToListOfQuestionResponseDTOs_Successfully() {
        Question question1 = new Question();
        question1.setId(1L);
        question1.setText("Qual é a capital do Brasil?");
        question1.setAnswer('A');

        Question question2 = new Question();
        question2.setId(2L);
        question2.setText("Qual é a capital da Argentina?");
        question2.setAnswer('B');

        List<Question> questions = List.of(question1, question2);

        List<QuestionResponseDTO> responseDTOs = questions.stream().map(questionMapper::toDTO).toList();

        assertNotNull(responseDTOs, "A lista de DTOs não deve ser nula.");
        assertEquals(2, responseDTOs.size(), "A lista de DTOs deve conter 2 elementos.");
        assertEquals(1L, responseDTOs.get(0).getId(), "O ID da primeira questão deve ser mapeado corretamente.");
        assertEquals("Qual é a capital do Brasil?", responseDTOs.get(0).getText(), "O texto da primeira questão deve ser mapeado corretamente.");
        assertEquals(2L, responseDTOs.get(1).getId(), "O ID da segunda questão deve ser mapeado corretamente.");
        assertEquals("Qual é a capital da Argentina?", responseDTOs.get(1).getText(), "O texto da segunda questão deve ser mapeado corretamente.");
    }

    @Test
    @DisplayName("Deve lidar com listas vazias ao mapear para QuestionResponseDTO")
    void shouldHandleEmptyListOfQuestions() {
        List<Question> emptyQuestions = Collections.emptyList();

        List<QuestionResponseDTO> responseDTOs = emptyQuestions.stream().map(questionMapper::toDTO).toList();

        assertNotNull(responseDTOs, "A lista de DTOs não deve ser nula.");
        assertTrue(responseDTOs.isEmpty(), "A lista de DTOs deve ser vazia.");
    }

    @Test
    @DisplayName("Deve lançar exceção ao mapear lista contendo questão nula")
    void shouldThrowException_WhenMappingListWithNullQuestion() {
        Question validQuestion = new Question();
        validQuestion.setId(1L);

        List<Question> questions = List.of(validQuestion);

        assertThrows(NullPointerException.class, () -> questions.stream().map(questionMapper::toDTO).toList());
    }
}
