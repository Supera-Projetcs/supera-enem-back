package com.supera.enem.mapper;


import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.controller.DTOS.Student.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface UserKeycloakMapper {

    UseKeycloakRegistrationDTO toKeycloakDTO(StudentDTO studentDTO);


}
