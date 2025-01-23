package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.controller.DTOS.AnswerResponseDTO;
import com.supera.enem.domain.Answer;
import com.supera.enem.domain.Question;
import com.supera.enem.domain.TestEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnswerMapperTest {

    private final AnswerMapper answerMapper = Mappers.getMapper(AnswerMapper.class);

    @Test
    @DisplayName("Deve mapear 'AnswerRequestDTO' para 'Answer' com valores válidos.")
    void shouldMapAnswerRequestDTOToAnswerSuccessfully() {

        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', 1L, 1L);

        Answer answer = answerMapper.toEntity(requestDTO);

        assertNotNull(answer, "A entidade 'Answer' não deve ser nula.");
        assertEquals('A', answer.getText(), "O texto deve ser mapeado corretamente.");
    }

    @Test
    @DisplayName("Deve lidar com 'AnswerRequestDTO' com valores nulos")
    void shouldHandleNullValuesInAnswerRequestDTO() {

        AnswerRequestDTO requestDTO = new AnswerRequestDTO('\0', null, null);

        Answer answer = answerMapper.toEntity(requestDTO);

        assertNotNull(answer, "A entidade 'Answer' não deve ser nula mesmo com DTO nulo.");
        assertNull(answer.getQuestion(), "Question deve ser nulo.");
        assertNull(answer.getTestEntity(), "TestEntity deve ser nulo.");
        assertEquals('\0', answer.getText(), "O texto deve ser o padrão inicial.");
    }

    @Test
    @DisplayName("Deve mapear Answer para 'AnswerResponseDTO' com valores válidos")
    void shouldMapAnswerToAnswerResponseDTOSuccessfully() {

        Answer answer = new Answer();
        answer.setId(1L);
        answer.setText('B');
        answer.setCorrect(true);

        Question question = new Question();
        question.setId(1L);
        answer.setQuestion(question);

        TestEntity testEntity = new TestEntity();
        testEntity.setId(1L);
        answer.setTestEntity(testEntity);

        AnswerResponseDTO responseDTO = answerMapper.toDTO(answer);

        assertNotNull(responseDTO, "O DTO não deve ser nulo.");
        assertEquals(1L, responseDTO.getId(), "O ID deve ser mapeado corretamente.");
        assertEquals('B', responseDTO.getText(), "O texto deve ser mapeado corretamente.");
        assertTrue(responseDTO.isCorrect(), "O valor de 'correct' deve ser mapeado corretamente.");
        assertEquals(1L, responseDTO.getQuestionId(), "O ID da questão deve ser mapeado corretamente.");
        assertEquals(1L, responseDTO.getTestId(), "O ID do teste deve ser mapeado corretamente.");
    }

    @Test
    @DisplayName("Deve lidar com 'Answer' com campos nulos ao converter para DTO")
    void shouldHandleNullValuesInAnswerEntity() {

        Answer answer = new Answer();
        answer.setId(1L);
        answer.setText('C');
        answer.setCorrect(false);
        answer.setQuestion(null);
        answer.setTestEntity(null);

        AnswerResponseDTO responseDTO = answerMapper.toDTO(answer);

        assertNotNull(responseDTO, "O DTO não deve ser nulo.");
        assertEquals(1L, responseDTO.getId(), "O ID deve ser mapeado corretamente.");
        assertEquals('C', responseDTO.getText(), "O texto deve ser mapeado corretamente.");
        assertFalse(responseDTO.isCorrect(), "A correção deve ser mapeada corretamente.");
        assertNull(responseDTO.getQuestionId(), "O ID da questão deve ser nulo.");
        assertNull(responseDTO.getTestId(), "O ID do teste deve ser nulo.");
    }

    @Test
    @DisplayName("Deve mapear lista de 'Answers' para 'AnswerResponseDTOs'")
    void shouldMapAnswerListToAnswerResponseDTOList() {

        Answer answer1 = new Answer();
        answer1.setId(1L);
        answer1.setText('A');
        answer1.setCorrect(true);

        Answer answer2 = new Answer();
        answer2.setId(2L);
        answer2.setText('B');
        answer2.setCorrect(false);

        List<Answer> answers = List.of(answer1, answer2);

        List<AnswerResponseDTO> responseDTOs = answerMapper.toDTOList(answers);

        assertNotNull(responseDTOs, "A lista de DTOs não deve ser nula.");
        assertEquals(2, responseDTOs.size(), "A lista deve conter 2 elementos.");
        assertEquals(1L, responseDTOs.get(0).getId(), "O primeiro ID deve ser mapeado corretamente.");
        assertEquals(2L, responseDTOs.get(1).getId(), "O segundo ID deve ser mapeado corretamente.");
    }

    @Test
    @DisplayName("Deve lidar com listas vazias ao converter para 'AnswerResponseDTOs'")
    void shouldHandleEmptyAnswerList() {

        List<Answer> emptyAnswers = Collections.emptyList();

        List<AnswerResponseDTO> responseDTOs = answerMapper.toDTOList(emptyAnswers);

        assertNotNull(responseDTOs, "A lista de DTOs não deve ser nula.");
        assertTrue(responseDTOs.isEmpty(), "A lista resultante deve ser vazia.");
    }

    @Test
    @DisplayName("Deve lidar com listas grandes de 'Answers' ao converter para DTOs")
    void shouldHandleLargeAnswerList() {

        List<Answer> largeAnswerList = new ArrayList<>();
        for (long i = 1; i <= 1000; i++) {
            Answer answer = new Answer();
            answer.setId(i);
            answer.setText('A');
            answer.setCorrect(i % 2 == 0);
            Question question = new Question();
            question.setId(i);
            answer.setQuestion(question);
            TestEntity test = new TestEntity();
            test.setId(i);
            answer.setTestEntity(test);
            largeAnswerList.add(answer);
        }

        List<AnswerResponseDTO> responseDTOs = answerMapper.toDTOList(largeAnswerList);

        assertNotNull(responseDTOs, "A lista resultante não deve ser nula.");
        assertEquals(1000, responseDTOs.size(), "A lista resultante deve conter 1000 elementos.");
        assertEquals(1L, responseDTOs.get(0).getId(), "O primeiro elemento deve ser mapeado corretamente.");
        assertEquals(1000L, responseDTOs.get(999).getId(), "O último elemento deve ser mapeado corretamente.");
    }

    @Test
    @DisplayName("Deve mapear AnswerRequestDTO para Answer com dados válidos")
    void shouldMapAnswerRequestDTOToAnswer_WhenDataIsValid() {
        AnswerRequestDTO requestDTO = new AnswerRequestDTO('A', 1L, 2L);

        Answer answer = answerMapper.toEntity(requestDTO);

        assertNotNull(answer);
        assertEquals('A', answer.getText());
    }

    @Test
    @DisplayName("Deve retornar null ao mapear AnswerRequestDTO nulo")
    void shouldReturnNull_WhenMappingNullAnswerRequestDTO() {
        Answer answer = answerMapper.toEntity(null);

        assertNull(answer);
    }

    @Test
    @DisplayName("Deve mapear Answer para AnswerResponseDTO com dados válidos")
    void shouldMapAnswerToAnswerResponseDTO_WhenDataIsValid() {
        Question question = new Question();
        question.setId(1L);

        TestEntity testEntity = new TestEntity();
        testEntity.setId(2L);

        Answer answer = new Answer();
        answer.setId(3L);
        answer.setText('B');
        answer.setCorrect(true);
        answer.setQuestion(question);
        answer.setTestEntity(testEntity);

        AnswerResponseDTO responseDTO = answerMapper.toDTO(answer);

        assertNotNull(responseDTO);
        assertEquals(3L, responseDTO.getId());
        assertEquals('B', responseDTO.getText());
        assertTrue(responseDTO.isCorrect());
        assertEquals(1L, responseDTO.getQuestionId());
        assertEquals(2L, responseDTO.getTestId());
    }

    @Test
    @DisplayName("Deve retornar null ao mapear Answer nulo")
    void shouldReturnNull_WhenMappingNullAnswer() {
        AnswerResponseDTO responseDTO = answerMapper.toDTO(null);

        assertNull(responseDTO);
    }

    @Test
    @DisplayName("Deve mapear lista de Answers para lista de AnswerResponseDTOs")
    void shouldMapAnswerListToAnswerResponseDTOList_WhenDataIsValid() {
        Question question = new Question();
        question.setId(1L);

        TestEntity testEntity = new TestEntity();
        testEntity.setId(2L);

        Answer answer1 = new Answer();
        answer1.setId(3L);
        answer1.setText('C');
        answer1.setCorrect(false);
        answer1.setQuestion(question);
        answer1.setTestEntity(testEntity);

        Answer answer2 = new Answer();
        answer2.setId(4L);
        answer2.setText('D');
        answer2.setCorrect(true);
        answer2.setQuestion(question);
        answer2.setTestEntity(testEntity);

        List<AnswerResponseDTO> responseDTOs = answerMapper.toDTOList(List.of(answer1, answer2));

        assertNotNull(responseDTOs);
        assertEquals(2, responseDTOs.size());
        assertEquals(3L, responseDTOs.get(0).getId());
        assertEquals(4L, responseDTOs.get(1).getId());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao mapear lista de Answers vazia")
    void shouldReturnEmptyList_WhenMappingEmptyAnswerList() {
        List<AnswerResponseDTO> responseDTOs = answerMapper.toDTOList(List.of());

        assertNotNull(responseDTOs);
        assertTrue(responseDTOs.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao mapear lista de Answers contendo null")
    void shouldThrowException_WhenMappingListWithNullAnswer() {
        Question question = new Question();
        question.setId(1L);

        TestEntity testEntity = new TestEntity();
        testEntity.setId(2L);

        Answer answer = new Answer();
        answer.setId(3L);
        answer.setText('C');
        answer.setCorrect(false);
        answer.setQuestion(question);
        answer.setTestEntity(testEntity);

        List<Answer> answers = List.of(answer, null);

        assertThrows(NullPointerException.class, () -> answerMapper.toDTOList(answers));
    }

    @Test
    @DisplayName("Deve lançar exceção ao mapear lista de Answers nula")
    void shouldThrowException_WhenMappingNullAnswerList() {
        assertThrows(NullPointerException.class, () -> answerMapper.toDTOList(null));
    }
}
