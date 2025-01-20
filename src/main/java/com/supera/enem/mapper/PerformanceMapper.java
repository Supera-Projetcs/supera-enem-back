package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.Performace.PerformaceResponseDTO;
import com.supera.enem.domain.Performance;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PerformanceMapper {
    PerformaceResponseDTO toDTO(Performance performance);

    List<PerformaceResponseDTO> toDTO_LIST(List<Performance> performances);
}
