package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.StudentSubject.StudentSubjectRequestDTO;
import com.supera.enem.controller.DTOS.StudentSubject.StudentSubjectResponseDTO;
import com.supera.enem.domain.StudentSubject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StudentSubjectMapper {
    StudentSubject toEntity(StudentSubjectRequestDTO dto);
    StudentSubjectResponseDTO toDto(StudentSubject entity);
}