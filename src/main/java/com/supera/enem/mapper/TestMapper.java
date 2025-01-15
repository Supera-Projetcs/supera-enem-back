package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.TestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TestMapper {
    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    @Mapping(target = "type", source = "type")
    TestResponseDTO toDTO(TestEntity testEntity);
}
