package com.supera.enem.service;

import com.supera.enem.controller.DTOS.SubjectDifficultyDTO;
import com.supera.enem.repository.PerformanceRepository;
import com.supera.enem.service.PerformanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PerformanceServiceTest {

    @InjectMocks
    private PerformanceService performanceService;

    @Mock
    private PerformanceRepository performanceRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnEasyDifficultyForHighPerformance() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(new Object[] { "Matemática", 85.0 });
        when(performanceRepository.findAveragePerformanceBySubject(1L)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Matemática", result.get(0).getSubjectName());
        assertEquals("EASY", result.get(0).getDifficulty());

        verify(performanceRepository, times(1)).findAveragePerformanceBySubject(1L);
    }

    @Test
    void shouldReturnMediumDifficultyForAveragePerformance() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(new Object[] { "Ciências", 55.0 });
        when(performanceRepository.findAveragePerformanceBySubject(1L)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ciências", result.get(0).getSubjectName());
        assertEquals("MEDIUM", result.get(0).getDifficulty());

        verify(performanceRepository, times(1)).findAveragePerformanceBySubject(1L);
    }

    @Test
    void shouldReturnHardDifficultyForLowPerformance() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(new Object[] { "História", 25.0 });
        when(performanceRepository.findAveragePerformanceBySubject(1L)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("História", result.get(0).getSubjectName());
        assertEquals("HARD", result.get(0).getDifficulty());

        verify(performanceRepository, times(1)).findAveragePerformanceBySubject(1L);
    }

    @Test
    void shouldReturnEmptyListWhenNoDataFound() {

        when(performanceRepository.findAveragePerformanceBySubject(1L)).thenReturn(List.of());

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(1L);

        assertNotNull(result);
        assertTrue(result.isEmpty(), "deve ser vazia quando não há dados.");
    }

    @Test
    void shouldThrowExceptionWhenStudentIdIsNull() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            performanceService.getSubjectDifficulties(null);
        });

        assertEquals("Student ID must not be null", exception.getMessage());
    }


    @Test
    void shouldReturnMediumDifficultyForBoundaryValues() {

        List<Object[]> mockResults = Arrays.<Object[]>asList(
                new Object[] { "Geografia", 70.0 },
                new Object[] { "Biologia", 40.0 }
        );
        when(performanceRepository.findAveragePerformanceBySubject(1L)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Geografia", result.get(0).getSubjectName());
        assertEquals("MEDIUM", result.get(0).getDifficulty());
        assertEquals("Biologia", result.get(1).getSubjectName());
        assertEquals("MEDIUM", result.get(1).getDifficulty());
    }

    @Test
    void shouldReturnMixedDifficultiesForMultipleSubjects() {
        List<Object[]> mockResults = Arrays.<Object[]>asList(
                new Object[] { "Matemática", 85.0 },
                new Object[] { "Ciências", 50.0 },
                new Object[] { "História", 30.0 }
        );
        when(performanceRepository.findAveragePerformanceBySubject(1L)).thenReturn(mockResults);

        List<SubjectDifficultyDTO> result = performanceService.getSubjectDifficulties(1L);

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
