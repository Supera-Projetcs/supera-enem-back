package com.supera.enem.service;

import com.supera.enem.BaseTest;
import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.mapper.TestMapper;
import com.supera.enem.repository.*;
import org.junit.jupiter.api.DisplayName;
import com.supera.enem.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestServiceTest extends BaseTest {

    @InjectMocks
    private TestService testService;

    @Mock
    private TestRepository testRepository;

    @Mock
    private TestMapper testMapper;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private WeeklyReportRepository weeklyReportRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("Deve lançar uma exceção quando o usuário não estiver autenticado.")
    void shouldThrowException_WhenUserIsNotAuthenticated() {

        mockUnauthenticatedUser();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.getCompletedTests());
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista de testes concluídos quando bem-sucedido")
    void shouldReturnCompletedTests_WhenSuccessful() {

        mockAuthenticatedUser();

        //entidades
        TestEntity testEntity1 = new TestEntity();
        testEntity1.setId(1L);
        testEntity1.setType(TestType.WEEKLY);

        TestEntity testEntity2 = new TestEntity();
        testEntity2.setId(2L);
        testEntity2.setType(TestType.WEEKLY);

        when(testRepository.findCompletedTests()).thenReturn(List.of(testEntity1, testEntity2));

        //mapper convertendo entidades para DTOs
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

    @Test
    @DisplayName("Deve retornar uma lista vazia de simulados quando não houver simulados completados.")
    void shouldReturnEmptyList_WhenNoCompletedTestsExist() {
        mockAuthenticatedUser();

        when(testRepository.findCompletedTests()).thenReturn(Collections.emptyList());

        List<TestResponseDTO> completedTests = testService.getCompletedTests();

        assertNotNull(completedTests, "A lista não deve ser nula.");
        assertTrue(completedTests.isEmpty(), "A lista deve estar vazia.");
        verify(testRepository, times(1)).findCompletedTests();
        verify(testMapper, never()).toDTO(any(TestEntity.class));
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o usuário está autenticado, mas não há simulados completados.")
    void shouldReturnEmptyList_WhenAuthenticatedButNoCompletedTests() {
        mockAuthenticatedUser();

        when(testRepository.findCompletedTests()).thenReturn(Collections.emptyList());

        List<TestResponseDTO> completedTests = testService.getCompletedTests();

        assertNotNull(completedTests, "A lista não deve ser nula.");
        assertTrue(completedTests.isEmpty(), "A lista deve estar vazia.");
        verify(testRepository, times(1)).findCompletedTests();
        verify(testMapper, never()).toDTO(any(TestEntity.class));
    }

    //

    @Test
    @DisplayName("Deve retornar o teste por ID quando o ID for válido e existir.")
    void shouldReturnTestById_WhenIdIsValidAndExists() {

        Long validId = 1L;

        TestEntity testEntity = new TestEntity();
        testEntity.setId(validId);

        TestResponseDTO testResponseDTO = new TestResponseDTO();
        testResponseDTO.setId(validId);

        when(testRepository.findById(validId)).thenReturn(Optional.of(testEntity));
        when(testMapper.toDTO(testEntity)).thenReturn(testResponseDTO);

        TestResponseDTO result = testService.getTestById(validId);

        assertNotNull(result, "O resultado não deve ser nulo.");
        assertEquals(validId, result.getId(), "O ID do resultado deve corresponder ao esperado.");
        verify(testRepository, times(1)).findById(validId);
        verify(testMapper, times(1)).toDTO(testEntity);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir.")
    void shouldThrowException_WhenIdDoesNotExist() {

        Long invalidId = 99L;

        when(testRepository.findById(invalidId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> testService.getTestById(invalidId)
        );

        assertEquals("Test not found with id: " + invalidId, exception.getMessage());
        verify(testRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Deve lançar 'IllegalArgumentException' quando o ID for negativo.")
    void shouldThrowException_WhenIdIsNegative() {

        Long negativeId = -1L;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testService.getTestById(negativeId)
        );

        assertEquals("Invalid ID: " + negativeId, exception.getMessage());
        verify(testRepository, never()).findById(negativeId);
    }

    @Test
    @DisplayName("Deve lançar 'IllegalArgumentException' quando o ID for nulo.")
    void shouldThrowException_WhenIdIsNull() {

        Long nullId = null;

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testService.getTestById(nullId)
        );

        assertEquals("ID must not be null", exception.getMessage());
        verify(testRepository, never()).findById(nullId);
    }

    @Test
    @DisplayName("Deve retornar o teste por ID quando o ID for o menor ID válido.")
    void shouldReturnTestById_WhenIdIsSmallestValid() {
        Long smallestValidId = 1L;

        TestEntity testEntity = new TestEntity();
        testEntity.setId(smallestValidId);

        TestResponseDTO testResponseDTO = new TestResponseDTO();
        testResponseDTO.setId(smallestValidId);

        when(testRepository.findById(smallestValidId)).thenReturn(Optional.of(testEntity));
        when(testMapper.toDTO(testEntity)).thenReturn(testResponseDTO);

        TestResponseDTO result = testService.getTestById(smallestValidId);

        assertNotNull(result, "O resultado não deve ser nulo.");
        assertEquals(smallestValidId, result.getId(), "O ID do resultado deve corresponder ao menor ID válido.");
        verify(testRepository, times(1)).findById(smallestValidId);
        verify(testMapper, times(1)).toDTO(testEntity);
    }

    @Test
    @DisplayName("Deve retornar o teste por ID quando o ID for um número muito grande.")
    void shouldReturnTestById_WhenIdIsVeryLarge() {
        Long veryLargeId = Long.MAX_VALUE;

        TestEntity testEntity = new TestEntity();
        testEntity.setId(veryLargeId);

        TestResponseDTO testResponseDTO = new TestResponseDTO();
        testResponseDTO.setId(veryLargeId);

        when(testRepository.findById(veryLargeId)).thenReturn(Optional.of(testEntity));
        when(testMapper.toDTO(testEntity)).thenReturn(testResponseDTO);

        TestResponseDTO result = testService.getTestById(veryLargeId);

        assertNotNull(result, "O resultado não deve ser nulo.");
        assertEquals(veryLargeId, result.getId(), "O ID do resultado deve corresponder ao ID muito grande.");
        verify(testRepository, times(1)).findById(veryLargeId);
        verify(testMapper, times(1)).toDTO(testEntity);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando tentar acessar 'getTestById' sem autenticação.")
    void shouldThrowException_WhenGetTestByIdWithoutAuthentication() {

        mockUnauthenticatedUser();

        Long testId = 1L;

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> testService.getTestById(testId)
        );

        assertEquals("User not authenticated", exception.getMessage());
    }


    //

    @Test
    @DisplayName("Deve gerar um teste com dados válidos.")
    void shouldGenerateTest_WhenDataIsValid() {
        mockAuthenticatedUser();

        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findByKeycloakId(anyString())).thenReturn(student);

        WeeklyReport weeklyReport = new WeeklyReport();
        Content content = new Content();
        content.setId(1L);
        weeklyReport.setContents(Set.of(content));
        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(anyLong())).thenReturn(Optional.of(weeklyReport));

        Question question = new Question();
        question.setId(1L);
        when(questionRepository.findRandomQuestionsByContent(anyLong(), eq(10))).thenReturn(List.of(question));

        TestResponseDTO responseDTO = new TestResponseDTO();
        responseDTO.setId(1L);
        when(testMapper.toDTO(any(TestEntity.class))).thenReturn(responseDTO);

        TestResponseDTO result = testService.generateTest();

        assertNotNull(result, "O resultado não deve ser nulo.");
        verify(testRepository, times(1)).save(any(TestEntity.class));
        verify(testMapper, times(1)).toDTO(any(TestEntity.class));
    }

}
