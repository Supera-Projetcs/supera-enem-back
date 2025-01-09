package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.Student.StudentDTO;
import com.supera.enem.controller.DTOS.Student.UpdateStudentDTO;
import com.supera.enem.domain.Student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;


@Mapper
public interface StudentMapper {
    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    @Mapping(target = "address", source = "address")
    Student toStudent(StudentDTO studentDTO);

    @Mapping(target = "id", ignore = true)  // Ignora o ID durante a atualização
    void updateStudentFromDTO(UpdateStudentDTO studentDTO, @MappingTarget Student student);
}