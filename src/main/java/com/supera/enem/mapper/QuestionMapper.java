package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.domain.Image;
import com.supera.enem.domain.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface QuestionMapper {

    QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

    default List<String> map(List<Image> images) {
        return images.stream()
                .map(Image::getPath)
                .collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "text", target = "text"),
            @Mapping(source = "answer", target = "answer"),
            @Mapping(source = "images", target = "images"),
            @Mapping(source = "answers", target = "answers")
    })
    QuestionResponseDTO toDTO(Question question);
}