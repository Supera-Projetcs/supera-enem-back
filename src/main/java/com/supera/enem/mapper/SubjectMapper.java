package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.SubjectDTO;
import com.supera.enem.domain.Subject;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
    List<SubjectDTO> toDtoList(List<Subject> entity);
}
