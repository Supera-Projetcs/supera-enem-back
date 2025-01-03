package com.supera.enem.mapper;

import com.supera.enem.dto.StudentDTO;
import com.supera.enem.domains.Student;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student toEntity(StudentDTO studentDTO);

    StudentDTO toDTO(Student student);

    void updateEntity(StudentDTO studentDTO, @MappingTarget Student student);
}