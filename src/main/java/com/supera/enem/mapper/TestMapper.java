package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.Test;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TestMapper {
    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

    TestResponseDTO toDTO(Test test);
}
