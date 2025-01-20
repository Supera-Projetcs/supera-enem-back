package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;
import com.supera.enem.controller.DTOS.WeeklyReportResponseDTO;
import com.supera.enem.domain.WeeklyReport;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WeeklyReportMapper {
    WeeklyReportMapper INSTANCE = Mappers.getMapper(WeeklyReportMapper.class);

    WeeklyReportDTO toDto(WeeklyReport weeklyReport);
    WeeklyReport toEntity(WeeklyReportRequestDTO reportResponseDTO);

    WeeklyReportDTO toDTO(WeeklyReport weeklyReport);
}
