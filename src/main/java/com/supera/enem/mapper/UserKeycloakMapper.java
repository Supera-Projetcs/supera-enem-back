package com.supera.enem.mapper;


import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.controller.DTOS.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface UserKeycloakMapper {
    UserKeycloakMapper INSTANCE = Mappers.getMapper(UserKeycloakMapper.class);
    UseKeycloakRegistrationDTO toKeycloakDTO(StudentDTO studentDTO);


}
