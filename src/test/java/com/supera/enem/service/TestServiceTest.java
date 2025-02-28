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

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.SecurityContextHolder;

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

    //getTestById

    @Test
    @DisplayName("Deve retornar o teste por ID quando o ID for válido e existir.")
    void shouldReturnTestById_WhenIdIsValidAndExists() {

        mockAuthenticatedUser();

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

        mockAuthenticatedUser();

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

        mockAuthenticatedUser();

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
        mockAuthenticatedUser();
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
        mockAuthenticatedUser();
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
        mockAuthenticatedUser();
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


    //generateTest

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

    @Test
    @DisplayName("Deve lançar uma exceção quando o keycloakId estiver ausente no JWT.")
    void shouldThrowException_WhenKeycloakIdIsAbsentInJwt() {

        mockAuthenticatedUserWithoutKeycloakId();

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("User not authenticated", exception.getMessage());
    }

    //simular autenticação sem keycloakId
    private void mockAuthenticatedUserWithoutKeycloakId() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Jwt jwtMock = mock(Jwt.class);
        when(jwtMock.getClaim("sub")).thenReturn(null);
        when(authentication.getPrincipal()).thenReturn(jwtMock);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o estudante não for encontrado no repositório.")
    void shouldThrowException_WhenStudentNotFound() {

        mockAuthenticatedUser();

        when(studentRepository.findByKeycloakId(anyString())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("Student not found", exception.getMessage());

        verify(studentRepository, times(1)).findByKeycloakId(anyString());
        verifyNoInteractions(testRepository, testMapper, weeklyReportRepository, questionRepository);
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando já existir um teste para a semana atual.")
    void shouldThrowException_WhenTestAlreadyExistsInCurrentWeek() {

        mockAuthenticatedUser();

        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findByKeycloakId(anyString())).thenReturn(student);

        when(testRepository.findByStudentAndDateBetween(any(Student.class), any(), any()))
                .thenReturn(List.of(new TestEntity()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("Test for the current week already exists.", exception.getMessage());

        verify(studentRepository, times(1)).findByKeycloakId(anyString());
        verify(testRepository, times(1)).findByStudentAndDateBetween(any(Student.class), any(), any());
        verifyNoInteractions(weeklyReportRepository, questionRepository, testMapper);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o relatório semanal não for encontrado no repositório.")
    void shouldThrowException_WhenWeeklyReportNotFound() {

        mockAuthenticatedUser();

        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findByKeycloakId(anyString())).thenReturn(student);

        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(anyLong()))
                .thenReturn(Optional.empty());

        when(testRepository.findByStudentAndDateBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("Weekly report not found", exception.getMessage());

        verify(studentRepository, times(1)).findByKeycloakId(anyString());
        verify(weeklyReportRepository, times(1)).findTopByStudentOrderByDateDesc(anyLong());
        verify(testRepository, times(1)).findByStudentAndDateBetween(any(), any(), any());
        verifyNoMoreInteractions(questionRepository, testMapper, testRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o relatório semanal não possuir conteúdos.")
    void shouldThrowException_WhenWeeklyReportIsEmpty() {

        mockAuthenticatedUser();

        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findByKeycloakId(anyString())).thenReturn(student);

        WeeklyReport weeklyReport = new WeeklyReport();
        weeklyReport.setContents(Collections.emptySet());
        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(anyLong()))
                .thenReturn(Optional.of(weeklyReport));

        when(testRepository.findByStudentAndDateBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("No content found in the weekly report", exception.getMessage());

        verify(studentRepository, times(1)).findByKeycloakId(anyString());
        verify(weeklyReportRepository, times(1)).findTopByStudentOrderByDateDesc(anyLong());
        verify(testRepository, times(1)).findByStudentAndDateBetween(any(), any(), any());
        verifyNoMoreInteractions(questionRepository, testMapper, testRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção quando um conteúdo não tem perguntas.")
    void shouldThrowException_WhenContentHasNoQuestions() {

        mockAuthenticatedUser();

        Student student = new Student();
        student.setId(1L);
        when(studentRepository.findByKeycloakId(anyString())).thenReturn(student);

        WeeklyReport weeklyReport = new WeeklyReport();
        Content content = new Content();
        content.setId(1L);
        weeklyReport.setContents(Set.of(content));
        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(anyLong()))
                .thenReturn(Optional.of(weeklyReport));

        when(testRepository.findByStudentAndDateBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        when(questionRepository.findRandomQuestionsByContent(eq(content.getId()), eq(10)))
                .thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> testService.generateTest());
        assertEquals("No questions found for content with id: 1", exception.getMessage());

        verify(studentRepository, times(1)).findByKeycloakId(anyString());
        verify(weeklyReportRepository, times(1)).findTopByStudentOrderByDateDesc(anyLong());
        verify(testRepository, times(1)).findByStudentAndDateBetween(any(), any(), any());
        verify(questionRepository, times(1)).findRandomQuestionsByContent(eq(content.getId()), eq(10));
        verifyNoInteractions(testMapper);
    }

    //getLastWeeklyReportByStudent

    @Test
    @DisplayName("Deve lançar exceção quando o estudante for null")
    void shouldThrowException_WhenStudentIsNull() {
        mockAuthenticatedUser();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> testService.getLastWeeklyReportByStudent(null)
        );

        assertEquals("Student must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o relatório semanal não for encontrado no repositório")
    void shouldThrowException_WhenWeeklyReportNotFoundInRepository() {
        mockAuthenticatedUser();
        Student student = new Student();
        student.setId(1L);

        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> testService.getLastWeeklyReportByStudent(student)
        );

        assertEquals("Weekly report not found", exception.getMessage());
        verify(weeklyReportRepository, times(1)).findTopByStudentOrderByDateDesc(student.getId());
    }

    @Test
    @DisplayName("Deve retornar o último relatório semanal para o estudante fornecido quando existente.")
    void shouldReturnLastWeeklyReport_WhenStudentHasValidWeeklyReport() {
        mockAuthenticatedUser();
        Student student = new Student();
        student.setId(1L);

        WeeklyReport weeklyReport = new WeeklyReport();
        weeklyReport.setId(1L);

        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId()))
                .thenReturn(Optional.of(weeklyReport));

        WeeklyReport result = testService.getLastWeeklyReportByStudent(student);

        assertNotNull(result, "O resultado não deve ser nulo.");
        assertEquals(weeklyReport.getId(), result.getId(), "O ID do relatório deve ser o mesmo retornado pelo repositório.");
        verify(weeklyReportRepository, times(1)).findTopByStudentOrderByDateDesc(student.getId());
    }

    @Test
    @DisplayName("Deve buscar o relatório mais recente quando múltiplos relatórios estão disponíveis")
    void shouldReturnMostRecentWeeklyReport_WhenMultipleReportsExist() {
        mockAuthenticatedUser();
        Student student = new Student();
        student.setId(1L);

        WeeklyReport olderReport = new WeeklyReport();
        olderReport.setId(1L);
        olderReport.setDate(java.sql.Date.valueOf(LocalDate.of(2025, 1, 1)));

        WeeklyReport recentReport = new WeeklyReport();
        recentReport.setId(2L);
        recentReport.setDate(java.sql.Date.valueOf(LocalDate.of(2025, 1, 15)));

        when(weeklyReportRepository.findTopByStudentOrderByDateDesc(student.getId()))
                .thenReturn(Optional.of(recentReport));

        WeeklyReport result = testService.getLastWeeklyReportByStudent(student);

        assertNotNull(result, "O resultado não deve ser nulo.");
        assertEquals(recentReport.getId(), result.getId(), "O relatório mais recente deve ser retornado.");
        assertEquals(recentReport.getDate(), result.getDate(), "A data do relatório mais recente deve ser correta.");
        verify(weeklyReportRepository, times(1)).findTopByStudentOrderByDateDesc(student.getId());
    }

    //getRandomQuestions

    @Test
    @DisplayName("Deve retornar todas as perguntas quando o tamanho da lista é menor ou igual ao limite")
    void shouldReturnAllQuestions_WhenListSizeIsLessThanOrEqualToLimit() {
        Question question1 = new Question();
        question1.setId(1L);
        Question question2 = new Question();
        question2.setId(2L);

        List<Question> questions = List.of(question1, question2);

        List<Question> result = testService.getRandomQuestions(questions, 3);

        assertEquals(2, result.size(), "Deve retornar todas as perguntas quando o tamanho da lista é menor ou igual ao limite.");
        assertTrue(result.containsAll(questions), "A lista retornada deve conter todas as perguntas originais.");
    }

    @Test
    @DisplayName("Deve retornar o número especificado de perguntas aleatórias")
    void shouldReturnSpecifiedNumberOfRandomQuestions() {
        Question question1 = new Question();
        question1.setId(1L);
        Question question2 = new Question();
        question2.setId(2L);
        Question question3 = new Question();
        question3.setId(3L);
        Question question4 = new Question();
        question4.setId(4L);

        List<Question> questions = List.of(question1, question2, question3, question4);

        List<Question> result = testService.getRandomQuestions(questions, 2);

        assertEquals(2, result.size(), "Deve retornar o número especificado de perguntas.");
        assertTrue(questions.containsAll(result), "As perguntas retornadas devem ser parte da lista original.");
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando a lista original for vazia")
    void shouldReturnEmptyList_WhenOriginalListIsEmpty() {
        List<Question> questions = List.of();

        List<Question> result = testService.getRandomQuestions(questions, 3);

        assertTrue(result.isEmpty(), "Deve retornar uma lista vazia quando a lista original estiver vazia.");
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o limite for zero")
    void shouldReturnEmptyList_WhenLimitIsZero() {
        Question question1 = new Question();
        question1.setId(1L);
        Question question2 = new Question();
        question2.setId(2L);

        List<Question> questions = List.of(question1, question2);

        List<Question> result = testService.getRandomQuestions(questions, 0);

        assertTrue(result.isEmpty(), "Deve retornar uma lista vazia quando o limite for zero.");
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o limite for negativo")
    void shouldReturnEmptyList_WhenLimitIsNegative() {
        Question question1 = new Question();
        question1.setId(1L);
        Question question2 = new Question();
        question2.setId(2L);

        List<Question> questions = List.of(question1, question2);

        List<Question> result = testService.getRandomQuestions(questions, -1);

        assertTrue(result.isEmpty(), "Deve retornar uma lista vazia quando o limite for negativo.");
    }

    @Test
    @DisplayName("Deve retornar perguntas únicas, eliminando duplicatas")
    void shouldReturnUniqueQuestions_WhenOriginalListHasDuplicates() {
        Question question1 = new Question();
        question1.setId(1L);
        Question question2 = new Question();
        question2.setId(2L);
        Question question3 = new Question();
        question3.setId(1L);

        List<Question> questions = List.of(question1, question2, question3);

        List<Question> result = testService.getRandomQuestions(questions, 3);

        assertEquals(2, result.size(), "Deve retornar perguntas únicas, eliminando duplicatas.");
        assertTrue(result.stream().distinct().count() == result.size(), "A lista retornada deve conter apenas perguntas únicas.");
    }

    //hasTestInCurrentWeek

    @Test
    @DisplayName("Deve retornar false quando não existem testes para a semana atual")
    void shouldReturnFalse_WhenNoTestsInCurrentWeek() {

        Student student = new Student();
        student.setId(1L);

        when(testRepository.findByStudentAndDateBetween(
                eq(student),
                any(java.sql.Date.class),
                any(java.sql.Date.class)
        )).thenReturn(Collections.emptyList());

        boolean result = testService.hasTestInCurrentWeek();

        assertFalse(result, "O método deve retornar false quando não existem testes para a semana atual.");
        verify(testRepository, times(1)).findByStudentAndDateBetween(any(), any(), any());
    }

    @Test
    @DisplayName("Deve retornar true quando existem testes para a semana atual")
    void shouldReturnTrue_WhenTestsExistInCurrentWeek() {

        Student student = new Student();
        student.setId(1L);

        TestEntity test1 = new TestEntity();
        test1.setId(1L);
        test1.setStudent(student);

        TestEntity test2 = new TestEntity();
        test2.setId(2L);
        test2.setStudent(student);

        List<TestEntity> testEntities = List.of(test1, test2);

        when(testRepository.findByStudentAndDateBetween(
                eq(student),
                any(java.sql.Date.class),
                any(java.sql.Date.class)
        )).thenReturn(testEntities);

        boolean result = testService.hasTestInCurrentWeek();

        assertTrue(result, "O método deve retornar true quando existem testes para a semana atual.");
        verify(testRepository, times(1)).findByStudentAndDateBetween(any(), any(), any());
    }

    //reprovado

    /*@Test
    @DisplayName("Deve retornar false quando o repositório retorna testes fora da semana atual")
    void shouldReturnFalse_WhenRepositoryReturnsTestsOutsideCurrentWeek() {

        Student student = new Student();
        student.setId(1L);

        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = now.with(java.time.DayOfWeek.SUNDAY);

        TestEntity testOutsideWeek = new TestEntity();
        testOutsideWeek.setId(1L);
        testOutsideWeek.setDate(java.sql.Date.valueOf(startOfWeek.minusDays(7)));

        when(testRepository.findByStudentAndDateBetween(
                eq(student),
                eq(java.sql.Date.valueOf(startOfWeek)),
                eq(java.sql.Date.valueOf(endOfWeek))
        )).thenReturn(List.of(testOutsideWeek));

        boolean result = testService.hasTestInCurrentWeek(student);

        assertFalse(result, "O método deve retornar false quando os testes retornados estão fora da semana atual.");

        verify(testRepository, times(1)).findByStudentAndDateBetween(
                eq(student),
                eq(java.sql.Date.valueOf(startOfWeek)),
                eq(java.sql.Date.valueOf(endOfWeek))
        );
    }*/

    @Test
    @DisplayName("Validar comportamento em uma semana sem limites de testes e várias chamadas ao método")
    void shouldReturnConsistentResults_WhenCalledMultipleTimes() {
        Student student = new Student();
        student.setId(1L);

        TestEntity testEntity1 = new TestEntity();
        testEntity1.setDate(java.sql.Date.valueOf(LocalDate.now().with(java.time.DayOfWeek.MONDAY)));

        TestEntity testEntity2 = new TestEntity();
        testEntity2.setDate(java.sql.Date.valueOf(LocalDate.now().with(java.time.DayOfWeek.WEDNESDAY)));

        List<TestEntity> currentWeekTests = List.of(testEntity1, testEntity2);

        when(testRepository.findByStudentAndDateBetween(
                eq(student),
                any(java.sql.Date.class),
                any(java.sql.Date.class))
        ).thenReturn(currentWeekTests);

        boolean resultFirstCall = testService.hasTestInCurrentWeek();
        boolean resultSecondCall = testService.hasTestInCurrentWeek();

        assertTrue(resultFirstCall, "A primeira chamada deve retornar true para testes na semana atual.");
        assertTrue(resultSecondCall, "A segunda chamada deve retornar true para testes na semana atual.");

        verify(testRepository, times(2)).findByStudentAndDateBetween(
                eq(student),
                any(java.sql.Date.class),
                any(java.sql.Date.class)
        );
    }

    @Test
    @DisplayName("Validar comportamento quando a data atual está nos limites do início e fim da semana")
    void shouldReturnTrue_WhenTestDateIsAtStartOrEndOfWeek() {
        Student student = new Student();
        student.setId(1L);

        LocalDate startOfWeek = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate endOfWeek = LocalDate.now().with(java.time.DayOfWeek.SUNDAY);

        TestEntity testAtStartOfWeek = new TestEntity();
        testAtStartOfWeek.setDate(java.sql.Date.valueOf(startOfWeek));

        TestEntity testAtEndOfWeek = new TestEntity();
        testAtEndOfWeek.setDate(java.sql.Date.valueOf(endOfWeek));

        List<TestEntity> boundaryTests = List.of(testAtStartOfWeek, testAtEndOfWeek);

        when(testRepository.findByStudentAndDateBetween(
                eq(student),
                any(java.sql.Date.class),
                any(java.sql.Date.class))
        ).thenReturn(boundaryTests);

        boolean result = testService.hasTestInCurrentWeek();

        assertTrue(result, "O método deve retornar true para testes que estão no início ou fim da semana atual.");

        verify(testRepository, times(1)).findByStudentAndDateBetween(
                eq(student),
                any(java.sql.Date.class),
                any(java.sql.Date.class)
        );
    }

}
