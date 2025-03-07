package com.supera.enem.service;

import com.supera.enem.controller.DTOS.AlitaRequestDTO;
import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;
import com.supera.enem.controller.DTOS.WeeklyReportResponseDTO;
import com.supera.enem.domain.Content;
import com.supera.enem.domain.Performance;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.WeeklyReportMapper;
import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.PerformanceRepository;
import com.supera.enem.repository.StudentSubjectRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import com.supera.enem.utils.FakeContentGenerator;
import jakarta.ws.rs.core.Link;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeeklyReportServiceTest {

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private PerformanceRepository performanceRepository;

    @Mock
    private StudentSubjectRepository studentSubjectRepository;

    @Mock
    private WeeklyReportMapper weeklyReportMapper;

    private Student student;
    private WeeklyReport weeklyReport;

    @InjectMocks
    private WeeklyReportService weeklyReportService;

    @Mock
    private AuthenticatedService authenticatedService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ContentRepository contentRepository;


    private WeeklyReport existingReport;
    private WeeklyReportDTO reportDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        student = new Student();
        student.setId(1L);
        student.setKeycloakId("keycloakId");
        student.setPreferredStudyDays(Set.of(Weekday.MONDAY, Weekday.WEDNESDAY));

        weeklyReport = new WeeklyReport();
        weeklyReport.setId(1L);
        weeklyReport.setStudent(student);
        weeklyReport.setContents(FakeContentGenerator.generateFakeContents());

        existingReport = new WeeklyReport();
        existingReport.setId(1L);
        existingReport.setStudent(student);
        existingReport.setContents(FakeContentGenerator.generateFakeContents());

        this.reportDTO = new WeeklyReportDTO();
        this.reportDTO.setId(1L);
        this.reportDTO.setDate(new Date());
        this.reportDTO.setContents(FakeContentGenerator.generateFakeContents());
//        this.reportDTO.se(student);
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
    @DisplayName("Deve converter WeeklyReport para WeeklyReportDTO corretamente")
    void shouldConvertWeeklyReportToDTO() {
        when(weeklyReportMapper.toDto(weeklyReport)).thenReturn(this.reportDTO);

        WeeklyReportDTO result = weeklyReportMapper.toDto(weeklyReport);

        assertNotNull(result);
        assertEquals(reportDTO.getId(), result.getId());
    }

    @Test
    @DisplayName("Deve retornar um relatório semanal existente se já houver um")
    void shouldReturnExistingWeeklyReport() {
        // Arrange
        when(authenticatedService.getAuthenticatedStudent()).thenReturn(student);
        List<WeeklyReport> reportList = new ArrayList<>();
        reportList.add(existingReport);
        when(weeklyReportRepository.findByStudentIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(reportList);
        when(weeklyReportMapper.toDto(existingReport)).thenReturn(reportDTO);


        WeeklyReportResponseDTO result = weeklyReportService.getWeeklyReport();

        assertNotNull(result);
        assertEquals(reportDTO.getId(), result.getId());
        verify(weeklyReportRepository, times(1)).findByStudentIdAndDateBetween(anyLong(), any(), any());
        verify(weeklyReportRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve criar e retornar um novo relatório semanal se não houver um existente")
    void shouldCreateNewWeeklyReportWhenNoneExists() {
        // Arrange
        when(authenticatedService.getAuthenticatedStudent()).thenReturn(student);
        when(weeklyReportRepository.findByStudentIdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(null);
        when(weeklyReportRepository.save(any())).thenReturn(weeklyReport);
        when(restTemplate.postForObject(anyString(), any(), eq(List.class)))
                .thenReturn(Collections.emptyList());


        // Act
        WeeklyReportResponseDTO result = weeklyReportService.getWeeklyReport();

        // Assert
        assertNotNull(result);
        assertEquals(reportDTO.getId(), result.getId());
        verify(weeklyReportRepository, times(1)).findByStudentIdAndDateBetween(anyLong(), any(), any());
        verify(weeklyReportRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve atualizar o WeeklyReport com novos conteúdos")
    void shouldUpdateWeeklyReportWithNewContents() {
        // Arrange
        when(authenticatedService.getAuthenticatedStudent()).thenReturn(student);
        when(weeklyReportRepository.findById(1L)).thenReturn(Optional.of(existingReport));

        // Dados de entrada
        WeeklyReportRequestDTO requestDTO = new WeeklyReportRequestDTO();
        requestDTO.setContentIds(Arrays.asList(101L, 102L));

        // Conteúdos mockados
        Content content1 = new Content();
        content1.setId(101L);
        Content content2 = new Content();
        content2.setId(102L);

        when(contentRepository.findAllById(requestDTO.getContentIds()))
                .thenReturn(Arrays.asList(content1, content2));

        WeeklyReport updatedReport = new WeeklyReport();
        updatedReport.setId(1L);
        updatedReport.setDate(requestDTO.getDate());
        updatedReport.setContents(new LinkedHashSet<>(Arrays.asList(content1, content2)));

        when(weeklyReportRepository.save(existingReport)).thenReturn(updatedReport);
        when(weeklyReportMapper.toDto(updatedReport)).thenReturn(reportDTO);

        // Act
        WeeklyReportDTO result = weeklyReportService.updateWeeklyReport(requestDTO, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(requestDTO.getDate(), updatedReport.getDate());
        assertEquals(2, updatedReport.getContents().size());
        verify(weeklyReportRepository).findById(1L);
        verify(contentRepository).findAllById(requestDTO.getContentIds());
        verify(weeklyReportRepository).save(existingReport);
        verify(weeklyReportMapper).toDto(updatedReport);
    }

    @Test
    @DisplayName("Deve limpar conteúdos quando contentIds estiver vazio")
    void shouldClearContentsWhenContentIdsIsEmpty() {
        // Arrange
        when(authenticatedService.getAuthenticatedStudent()).thenReturn(student);
        when(weeklyReportRepository.findById(1L)).thenReturn(Optional.of(existingReport));

        WeeklyReportRequestDTO requestDTO = new WeeklyReportRequestDTO();
        requestDTO.setContentIds(Collections.emptyList());

        existingReport.setContents(new LinkedHashSet<>(Arrays.asList(new Content(), new Content())));

        WeeklyReport updatedReport = new WeeklyReport();
        updatedReport.setId(1L);
        updatedReport.setDate(requestDTO.getDate());
        updatedReport.setContents(Collections.emptySet());

        when(weeklyReportRepository.save(existingReport)).thenReturn(updatedReport);
        when(weeklyReportMapper.toDto(updatedReport)).thenReturn(reportDTO);

        // Act
        WeeklyReportDTO result = weeklyReportService.updateWeeklyReport(requestDTO, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(updatedReport.getContents().isEmpty());
        verify(weeklyReportRepository).findById(1L);
        verify(weeklyReportRepository).save(existingReport);
        verify(weeklyReportMapper).toDto(updatedReport);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o WeeklyReport não existir")
    void shouldThrowExceptionWhenWeeklyReportNotFound() {
        // Arrange
        when(weeklyReportRepository.findById(999L)).thenReturn(Optional.empty());

        WeeklyReportRequestDTO requestDTO = new WeeklyReportRequestDTO();

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            weeklyReportService.updateWeeklyReport(requestDTO, 999L);
        });

        assertEquals("WeeklyReport not found with ID: 999", exception.getMessage());
        verify(weeklyReportRepository).findById(999L);
        verify(weeklyReportRepository, never()).save(any());
    }

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
