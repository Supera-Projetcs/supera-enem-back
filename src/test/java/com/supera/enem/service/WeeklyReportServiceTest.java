package com.supera.enem.service;

import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.repository.WeeklyReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeeklyReportServiceTest {

    @InjectMocks
    private WeeklyReportService weeklyReportService;

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        student = new Student();
        student.setId(1L);
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
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weeklyReportService.getWeeklyReportById(null, student)
        );

        assertEquals("ID must not be null", exception.getMessage());
    }
}
