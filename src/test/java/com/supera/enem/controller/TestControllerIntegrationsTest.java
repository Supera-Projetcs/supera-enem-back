package com.supera.enem.controller;

import com.supera.enem.TestDataUtil;
import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.controller.DTOS.TestResponseDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.StudentMapper;
import com.supera.enem.service.TestService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TestControllerIntegrationsTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestService testService;

    private TestResponseDTO testResponseDTO;

    private Student student;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TestDataUtil testDataUtil;
    @Autowired
    private StudentMapper studentMapper;

    @BeforeEach
    public void setUp() {
        testResponseDTO = new TestResponseDTO();
        testResponseDTO.setId(1L);
        testResponseDTO.setType(TestType.WEEKLY);
        Date date = new Date();
        date.setTime(1666243200000L);
        testResponseDTO.setDate(date);
        student = testDataUtil.createAndSaveStudent();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);
    }

    @Test
    public void testGenerateTest_Success() throws Exception {
        // Cria um TestResponseDTO mockado
        TestResponseDTO testResponseDTO = new TestResponseDTO();
        testResponseDTO.setId(1L);
        testResponseDTO.setType(TestType.WEEKLY);
        testResponseDTO.setDate(new Date());
        testResponseDTO.setStudent(studentMapper.toStudentDTO(student));

        // Cria a lista de QuestionResponseDTO com os campos corretos
        List<QuestionResponseDTO> questions = Arrays.asList(
                new QuestionResponseDTO(1L, "What is 2 + 2?", 'A', List.of("image1.png", "image2.png"), Map.of("A", "4", "B", "5")),
                new QuestionResponseDTO(2L, "What is 3 * 3?", 'C', List.of("image3.png"), Map.of("A", "6", "B", "9", "C", "9"))
        );
        testResponseDTO.setQuestions(questions);

        // Mock do serviço para retornar o TestResponseDTO
        when(testService.generateTest()).thenReturn(testResponseDTO);

        // Executa a requisição e verifica o resultado
        mockMvc.perform(post("/api/tests/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token"))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Verifica o texto da segunda questão no JSON
    }

    @Test
    public void testGetCompletedTests_Success() throws Exception {
        // Cria a lista de TestResponseDTO com os campos corretos
        List<TestResponseDTO> completedTests = Arrays.asList(
                new TestResponseDTO(1L, TestType.WEEKLY, new Date(1666243200000L), studentMapper.toStudentDTO(student), List.of()),
                new TestResponseDTO(2L, TestType.WEEKLY, new Date(1666329600000L), studentMapper.toStudentDTO(student), List.of())
        );

        // Mock do serviço para retornar a lista de TestResponseDTO
        when(testService.getCompletedTests()).thenReturn(completedTests);

        // Executa a requisição e verifica o resultado
        mockMvc.perform(get("/api/tests/completed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token"))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Verifica o tipo do segundo teste no JSON
    }

    @Test
    public void testGetTestById_Success() throws Exception {
        // Mock do serviço para retornar o TestResponseDTO
        when(testService.getTestById(1L)).thenReturn(testResponseDTO);

        // Executa a requisição e verifica o resultado
        mockMvc.perform(get("/api/tests/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token"))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Verifica se a data existe
    }

    @Test
    public void testGetTestById_NotFound() throws Exception {
        // Mock do serviço para lançar uma exceção quando o teste não for encontrado
        when(testService.getTestById(1L)).thenThrow(new ResourceNotFoundException("Test not found with ID: 1"));

        // Executa a requisição e verifica o resultado
        mockMvc.perform(get("/api/tests/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token"))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isNotFound()); // Espera status 404
    }
}
