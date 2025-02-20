package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.Student.StudentDTO;
import com.supera.enem.controller.DTOS.Student.UpdateStudentDTO;
import com.supera.enem.domain.Student;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface StudentMapper {
    @Mapping(target = "address", source = "address")
    Student toStudent(StudentDTO studentDTO);

//    @Mapping(target = "id", ignore = true)
//    void updateStudentFromDTO(UpdateStudentDTO studentDTO, @MappingTarget Student student);
    @Mapping(target = "id", ignore = true)  // Evita modificar o ID
    void updateStudentFromDTO(UpdateStudentDTO dto, @MappingTarget Student student);

}