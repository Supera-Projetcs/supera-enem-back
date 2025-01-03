package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.domain.Question;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuestionMapper {
    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    QuestionResponseDTO toDTO(Question question);

    Question toEntity(QuestionResponseDTO dto);
}