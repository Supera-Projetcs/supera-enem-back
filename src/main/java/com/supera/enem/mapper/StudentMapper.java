package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.StudentDTO;
import com.supera.enem.domain.Student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    @Mapping(target = "address", source = "address")
    Student toStudent(StudentDTO studentDTO);
}