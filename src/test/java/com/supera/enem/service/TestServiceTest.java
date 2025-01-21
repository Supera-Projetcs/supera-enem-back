package com.supera.enem.service;

import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.TestEntity;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.mapper.TestMapper;
import com.supera.enem.repository.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServiceTest {

    @InjectMocks
    private TestService testService;

    @Mock
    private TestRepository testRepository;

    @Mock
    private TestMapper testMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return a list of completed tests when successful")
    void shouldReturnCompletedTests_WhenSuccessful() {

        TestEntity testEntity1 = new TestEntity();
        testEntity1.setId(1L);
        testEntity1.setType(TestType.WEEKLY);

        TestEntity testEntity2 = new TestEntity();
        testEntity2.setId(2L);
        testEntity2.setType(TestType.WEEKLY);

        when(testRepository.findCompletedTests()).thenReturn(List.of(testEntity1, testEntity2));

        TestResponseDTO testResponseDTO1 = new TestResponseDTO();
        testResponseDTO1.setId(1L);
        testResponseDTO1.setType(TestType.WEEKLY);

        TestResponseDTO testResponseDTO2 = new TestResponseDTO();
        testResponseDTO2.setId(2L);
        testResponseDTO2.setType(TestType.WEEKLY);

        when(testMapper.toDTO(testEntity1)).thenReturn(testResponseDTO1);
        when(testMapper.toDTO(testEntity2)).thenReturn(testResponseDTO2);


        List<TestResponseDTO> completedTests = testService.getCompletedTests();


        assertNotNull(completedTests, "A lista de testes completados não deve ser nula.");
        assertEquals(2, completedTests.size(), "A lista deve conter 2 itens.");
        assertTrue(completedTests.contains(testResponseDTO1), "A lista deve conter o DTO do teste 1.");
        assertTrue(completedTests.contains(testResponseDTO2), "A lista deve conter o DTO do teste 2.");
        verify(testRepository, times(1)).findCompletedTests();
        verify(testMapper, times(2)).toDTO(any(TestEntity.class));
    }
}
