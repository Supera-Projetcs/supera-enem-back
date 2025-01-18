package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.controller.DTOS.AnswerResponseDTO;
import com.supera.enem.domain.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    Answer toEntity(AnswerRequestDTO answerRequestDTO);

    @Mapping(target = "questionId", source = "question.id") // Mapeia o ID da questão
    @Mapping(target = "testId", source = "testEntity.id")
    AnswerResponseDTO toDTO(Answer answer);

    List<AnswerResponseDTO> toDTOList(List<Answer> answers);
}
