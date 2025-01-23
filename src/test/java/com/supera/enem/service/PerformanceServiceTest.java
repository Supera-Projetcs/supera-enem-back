package com.supera.enem.service;

import com.supera.enem.controller.DTOS.SubjectDifficultyDTO;
import com.supera.enem.repository.PerformanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PerformanceServiceTest {

    private static final Long STUDENT_ID = 1L;

    @InjectMocks
    private PerformanceService performanceService;

    @Mock
    private PerformanceRepository performanceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve retornar dificuldade EASY para alta performance")
    void shouldReturnEasyDifficultyForHighPerformance() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(new Object[]{"Matemática", 85.0});
        when(performanceRepository.findAveragePerformanceBySubject(STUDENT_ID)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(STUDENT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Matemática", result.get(0).getSubjectName());
        assertEquals("EASY", result.get(0).getDifficulty());

        verify(performanceRepository, times(1)).findAveragePerformanceBySubject(STUDENT_ID);
    }

    @Test
    @DisplayName("Deve retornar dificuldade MEDIUM para performance média")
    void shouldReturnMediumDifficultyForAveragePerformance() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(new Object[]{"Ciências", 55.0});
        when(performanceRepository.findAveragePerformanceBySubject(STUDENT_ID)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(STUDENT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ciências", result.get(0).getSubjectName());
        assertEquals("MEDIUM", result.get(0).getDifficulty());

        verify(performanceRepository, times(1)).findAveragePerformanceBySubject(STUDENT_ID);
    }

    @Test
    @DisplayName("Deve retornar dificuldade HARD para baixa performance")
    void shouldReturnHardDifficultyForLowPerformance() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(new Object[]{"História", 25.0});
        when(performanceRepository.findAveragePerformanceBySubject(STUDENT_ID)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(STUDENT_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("História", result.get(0).getSubjectName());
        assertEquals("HARD", result.get(0).getDifficulty());

        verify(performanceRepository, times(1)).findAveragePerformanceBySubject(STUDENT_ID);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando nenhum dado for encontrado")
    void shouldReturnEmptyListWhenNoDataFound() {

        when(performanceRepository.findAveragePerformanceBySubject(STUDENT_ID)).thenReturn(List.of());

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(STUDENT_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "A lista deve ser vazia quando não há dados.");
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ID do estudante for nulo")
    void shouldThrowExceptionWhenStudentIdIsNull() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            performanceService.getSubjectDifficulties(null);
        });

        assertEquals("Student ID must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção para valores de performance inválidos")
    void shouldThrowExceptionForInvalidPerformanceValues() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(new Object[]{"Matemática", -10.0});
        when(performanceRepository.findAveragePerformanceBySubject(STUDENT_ID)).thenReturn(mockResults);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> performanceService.getSubjectDifficulties(STUDENT_ID)
        );

        assertEquals("Invalid performance value: -10.0", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar dificuldade MEDIUM para valores nos limites (70 e 40)")
    void shouldReturnMediumDifficultyForBoundaryValues() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(
                new Object[]{"Geografia", 70.0},
                new Object[]{"Biologia", 40.0}
        );
        when(performanceRepository.findAveragePerformanceBySubject(STUDENT_ID)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(STUDENT_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Geografia", result.get(0).getSubjectName());
        assertEquals("MEDIUM", result.get(0).getDifficulty());
        assertEquals("Biologia", result.get(1).getSubjectName());
        assertEquals("MEDIUM", result.get(1).getDifficulty());
    }

    @Test
    @DisplayName("Deve retornar dificuldades misturadas para múltiplas disciplinas")
    void shouldReturnMixedDifficultiesForMultipleSubjects() {
        List<Object[]> mockResults = Arrays.<Object[]>asList(
                new Object[]{"Matemática", 85.0},
                new Object[]{"Ciências", 50.0},
                new Object[]{"História", 30.0}
        );
        when(performanceRepository.findAveragePerformanceBySubject(STUDENT_ID)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(STUDENT_ID);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Matemática", result.get(0).getSubjectName());
        assertEquals("EASY", result.get(0).getDifficulty());
        assertEquals("Ciências", result.get(1).getSubjectName());
        assertEquals("MEDIUM", result.get(1).getDifficulty());
        assertEquals("História", result.get(2).getSubjectName());
        assertEquals("HARD", result.get(2).getDifficulty());
    }
}
