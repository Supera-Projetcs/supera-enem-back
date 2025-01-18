package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.Image;
import com.supera.enem.domain.TestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TestMapper {
    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    @Mapping(target = "type", source = "type")
    TestResponseDTO toDTO(TestEntity testEntity);

    default List<String> mapImagesToStrings(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
    }
}
