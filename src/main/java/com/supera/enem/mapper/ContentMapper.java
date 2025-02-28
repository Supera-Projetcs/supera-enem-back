package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.ContentDTO;
import com.supera.enem.domain.Content;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContentMapper {
    List<ContentDTO> toDtoList(List<Content> entity);
}
