package com.supera.enem.mapper;


import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.controller.DTOS.Student.StudentRequestDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface UserKeycloakMapper {

    UseKeycloakRegistrationDTO toKeycloakDTO(StudentRequestDTO studentDTO);

}
