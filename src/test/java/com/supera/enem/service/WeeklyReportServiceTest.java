package com.supera.enem.service;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;
import com.supera.enem.domain.Content;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.WeeklyReportMapper;
import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import jakarta.ws.rs.core.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeeklyReportServiceTest {

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private WeeklyReportMapper weeklyReportMapper;

    private Student student;
    private WeeklyReport weeklyReport;

    @InjectMocks
    private WeeklyReportService weeklyReportService;

    @Mock
    private AuthenticatedService authenticatedService;

    @Mock
    private ContentRepository contentRepository;

    private WeeklyReportRequestDTO weeklyReportRequestDTO;

    private WeeklyReport updatedReport;
    private WeeklyReportDTO weeklyReportDTO;
    private Student authenticatedStudent;
    private Set<Content> contents;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        student = new Student();
        student.setId(1L);
        student.setKeycloakId("keycloakId");

        weeklyReport = new WeeklyReport();
        weeklyReport.setId(1L);
        weeklyReport.setStudent(student);

        updatedReport = new WeeklyReport();
        updatedReport.setId(1L);
        updatedReport.setStudent(authenticatedStudent);

        weeklyReportDTO = new WeeklyReportDTO();
        weeklyReportDTO.setId(1L);

        contents = new LinkedHashSet<>();
        Content content1 = new Content();
        content1.setId(1L);
        Content content2 = new Content();
        content2.setId(1L);
        contents.add(content1);
        contents.add(content2);
    }


    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o Student for nulo")
    void shouldThrowExceptionWhenStudentIsNull() {
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            weeklyReportService.getWeeklyReportById(1L, null);
        });

        assertEquals("WeeklyReport not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar WeeklyReportDTO quando ID e Student são válidos")
    void shouldReturnWeeklyReportWhenIdAndStudentAreValid() {
        WeeklyReportDTO weeklyReportDTO = new WeeklyReportDTO();
        weeklyReportDTO.setId(1L);

        when(weeklyReportRepository.findByIdAndStudent(eq(1L), any(Student.class)))
                .thenReturn(Optional.of(weeklyReport));

        when(weeklyReportMapper.toDto(weeklyReport)).thenReturn(weeklyReportDTO);

        WeeklyReportDTO result = weeklyReportService.getWeeklyReportById(1L, student);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(weeklyReportRepository, times(1)).findByIdAndStudent(1L, student);
    }


    @Test
    @DisplayName("Deve lançar ResourceNotFoundException se o ID não existir")
    void shouldThrowExceptionWhenIdDoesNotExist() {
        when(weeklyReportRepository.findByIdAndStudent(1L, student)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            weeklyReportService.getWeeklyReportById(1L, student);
        });

        assertEquals("WeeklyReport not exist", exception.getMessage());
    }

    @Test
    @DisplayName("Should update weekly report successfully with valid input")
    void shouldUpdateWeeklyReportSuccessfully() {
        // Arrange
        when(authenticatedService.getAuthenticatedStudent()).thenReturn(authenticatedStudent);
        when(weeklyReportRepository.findById(1L)).thenReturn(Optional.of(weeklyReport));
        when(contentRepository.findAllById(weeklyReportRequestDTO.getContentIds())).thenReturn(contents);
        when(weeklyReportRepository.save(existingReport)).thenReturn(updatedReport);
        when(weeklyReportMapper.toDTO(updatedReport)).thenReturn(weeklyReportDTO);

        // Act
        WeeklyReportDTO result = weeklyReportService.updateWeeklyReport(weeklyReportRequestDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals("2023-10-01", result.getDate());
        verify(weeklyReportRepository, times(1)).findById(1L);
        verify(contentRepository, times(1)).findAllById(weeklyReportRequestDTO.getContentIds());
        verify(weeklyReportRepository, times(1)).save(existingReport);
        verify(weeklyReportMapper, times(1)).toDTO(updatedReport);
    }

//    @Test
//    @DisplayName("Deve retornar todos os relatórios semanais de um estudante.")
//    void shouldReturnWeeklyReportsByStudent() {
//        WeeklyReport report1 = new WeeklyReport();
//        report1.setId(1L);
//
//        WeeklyReport report2 = new WeeklyReport();
//        report2.setId(2L);
//
//        when(weeklyReportRepository.findByStudent(student)).thenReturn(List.of(report1, report2));
//
//        List<WeeklyReport> reports = weeklyReportService.getWeeklyReportsByStudent(student);
//
//        assertNotNull(reports);
//        assertEquals(2, reports.size());
//        assertEquals(1L, reports.get(0).getId());
//        assertEquals(2L, reports.get(1).getId());
//        verify(weeklyReportRepository, times(1)).findByStudent(student);
//    }

//    @Test
//    @DisplayName("Deve retornar uma lista vazia quando o estudante não possui relatórios semanais.")
//    void shouldReturnEmptyListWhenNoReportsForStudent() {
//        when(weeklyReportRepository.findByStudent(student)).thenReturn(List.of());
//
//        List<WeeklyReport> reports = weeklyReportService.getWeeklyReportsByStudent(student);
//
//        assertNotNull(reports);
//        assertTrue(reports.isEmpty(), "A lista de relatórios deve ser vazia.");
//        verify(weeklyReportRepository, times(1)).findByStudent(student);
//    }

//    @Test
//    @DisplayName("Deve retornar um relatório semanal específico pelo ID e estudante.")
//    void shouldReturnWeeklyReportByIdAndStudent() {
//        WeeklyReport report = new WeeklyReport();
//        report.setId(1L);
//
//        when(weeklyReportRepository.findByIdAndStudent(1L, student)).thenReturn(report);
//
//        WeeklyReport foundReport = weeklyReportService.getWeeklyReportById(1L, student);
//
//        assertNotNull(foundReport);
//        assertEquals(1L, foundReport.getId());
//        verify(weeklyReportRepository, times(1)).findByIdAndStudent(1L, student);
//    }
//
//    @Test
//    @DisplayName("Deve retornar null quando o relatório não for encontrado pelo ID e estudante.")
//    void shouldReturnNullWhenWeeklyReportNotFoundByIdAndStudent() {
//        when(weeklyReportRepository.findByIdAndStudent(999L, student)).thenReturn(null);
//
//        WeeklyReport report = weeklyReportService.getWeeklyReportById(999L, student);
//
//        assertNull(report, "O relatório deve ser nulo quando não encontrado.");
//        verify(weeklyReportRepository, times(1)).findByIdAndStudent(999L, student);
//    }
//
//    @Test
//    @DisplayName("Deve lançar IllegalArgumentException quando o estudante for nulo")
//    void shouldThrowExceptionWhenStudentIsNull() {
//        IllegalArgumentException exception = assertThrows(
//                IllegalArgumentException.class,
//                () -> weeklyReportService.getWeeklyReportsByStudent(null)
//        );
//
//        assertEquals("Student must not be null", exception.getMessage());
//    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando ID for nulo.")
    void shouldThrowExceptionWhenIdIsNull() {
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> weeklyReportService.getWeeklyReportById(null, student)
        );

        assertEquals("WeeklyReport not exist", exception.getMessage());
    }
}
