package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.Image;
import com.supera.enem.domain.TestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TestMapper {
    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "totalQuestions", source = "totalQuestions")
    @Mapping(target = "totalCorrectAnswers", source = "totalCorrectAnswers")
    @Mapping(target = "contents", source = "uniqueContents")
    TestResponseDTO toDTO(TestEntity testEntity);

    TestEntity toEntity(TestResponseDTO testResponseDTO);

    // Método para converter List<Image> em List<String>
    default List<String> mapImagesToStrings(List<Image> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }
        return images.stream()
                .map(Image::getUrl)
                .collect(Collectors.toList());
    }

    // Método para converter List<String> em List<Image>
    default List<Image> mapStringsToImages(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return List.of();
        }
        return imageUrls.stream()
                .map(url -> {
                    Image image = new Image();
                    image.setUrl(url);
                    return image;
                })
                .collect(Collectors.toList());
    }
}
