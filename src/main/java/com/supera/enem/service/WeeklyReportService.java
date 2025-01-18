package com.supera.enem.service;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;
import com.supera.enem.controller.DTOS.WeeklyReportResponseDTO;
import com.supera.enem.domain.Content;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.WeeklyReportMapper;
import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class WeeklyReportService {

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;
    @Autowired
    private AuthenticatedService authenticatedService;

    private final WeeklyReportMapper weeklyReportMapper = WeeklyReportMapper.INSTANCE;
    @Autowired
    private ContentRepository contentRepository;

    public List<WeeklyReport> getWeeklyReportsByStudent(Student student) {
        return weeklyReportRepository.findByStudent(student);
    }

    public WeeklyReport getWeeklyReportById(Long id, Student student) {
        return weeklyReportRepository.findByIdAndStudent(id, student)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklyReport not found for the given ID and student"));
    }

    public WeeklyReportDTO updateWeeklyReport(WeeklyReportRequestDTO weeklyReportRequestDTO, Long id) {

        Student student = authenticatedService.getAuthenticatedStudent();

        WeeklyReport existingReport = weeklyReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklyReport not found with ID: " + id));

        existingReport.setDate(weeklyReportRequestDTO.getDate());

        if (weeklyReportRequestDTO.getContentIds() != null && !weeklyReportRequestDTO.getContentIds().isEmpty()) {
            Set<Content> updatedContents = new LinkedHashSet<>(contentRepository.findAllById(weeklyReportRequestDTO.getContentIds()));
            existingReport.setContents(updatedContents);
        } else {
            existingReport.getContents().clear();
        }

        WeeklyReport updatedReport = weeklyReportRepository.save(existingReport);

        return weeklyReportMapper.toDTO(updatedReport);
    }


}
