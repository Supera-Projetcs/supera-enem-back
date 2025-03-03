package com.supera.enem.controller;

import com.supera.enem.TestDataUtil;
import com.supera.enem.controller.DTOS.ContentDTO;
import com.supera.enem.controller.DTOS.Performace.PerformaceResponseDTO;
import com.supera.enem.controller.DTOS.SubjectDifficultyDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.Subject;
import com.supera.enem.exception.BusinessException;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.service.PerformanceService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PerformanceControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PerformanceService performanceService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private List<PerformaceResponseDTO> performanceResponses;

    @Autowired
    private TestDataUtil testDataUtil;

    private Student student;

    @BeforeEach
    public void setUp() {
        student = testDataUtil.createAndSaveStudent();

        Subject subject = testDataUtil.createAndSaveSubject();
        ContentDTO contentDTO = new ContentDTO(1L, "Mathematics", 0.5, 0.5, subject);
        performanceResponses = Arrays.asList(
            new PerformaceResponseDTO(1, 80.0, LocalDateTime.now(), contentDTO),
            new PerformaceResponseDTO(2, 60.0, LocalDateTime.now(), contentDTO),
            new PerformaceResponseDTO(3, 40.0, LocalDateTime.now(), contentDTO)
        );

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);
    }

    @Test
    public void testCreateInitialPerformance_Success() throws Exception {
        // Mock do serviço para retornar a lista de PerformaceResponseDTO
        when(performanceService.createInitialPerformance(eq(1L), anyList())).thenReturn(performanceResponses);

        // Corpo da requisição em formato JSON
        String requestBody = "[{\"subjectId\": 1, \"performaceValue\": 80.0}, {\"subjectId\": 2, \"performaceValue\": 60.0}, {\"subjectId\": 3, \"performaceValue\": 40.0}]";

        // Executa a requisição e verifica o resultado
        mockMvc.perform(post("/api/performance/initial-performace/{studentId}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)) // Define o corpo da requisição
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk());

        // Verifica se o método do serviço foi chamado
        verify(performanceService, times(1)).createInitialPerformance(eq(1L), anyList());
    }

    @Test
    public void testCreateInitialPerformance_StudentNotFound() throws Exception {
        // Mock do serviço para lançar uma exceção quando o estudante não for encontrado
        when(performanceService.createInitialPerformance(eq(1L), anyList()))
                .thenThrow(new ResourceNotFoundException("Estudante não encontrado"));

        String requestBody = "[{\"subjectId\": 1, \"performaceValue\": 80.0}]";

        mockMvc.perform(post("/api/performance/initial-performance/{studentId}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)) // Define o corpo da requisição
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isNotFound()); // Espera status 404
    }

    @Test
    public void testCreateInitialPerformance_BusinessException() throws Exception {
        // Mock do serviço para lançar uma exceção de negócio
        when(performanceService.createInitialPerformance(eq(1L), anyList()))
                .thenThrow(new BusinessException("Não tem performance inicial para todas as matérias."));

        String requestBody = "[{\"subjectId\": 1, \"performaceValue\": 80.0}]";

        mockMvc.perform(post("/api/performance/initial-performance/{studentId}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)) // Define o corpo da requisição
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isNotFound()); // Espera status 400
    }

    @Test
    public void testGetSubjectDifficulties_Success() throws Exception {
        // Mock do serviço para retornar a lista de SubjectDifficultyDTO
        List<SubjectDifficultyDTO> subjectDifficulties = Arrays.asList(
                new SubjectDifficultyDTO("Mathematics", 75.0),
                new SubjectDifficultyDTO("Physics", 50.0),
                new SubjectDifficultyDTO("Chemistry", 35.0)
        );
        when(performanceService.getSubjectDifficulties(eq(1L))).thenReturn(subjectDifficulties);

        mockMvc.perform(get("/api/performance/difficulties/{studentId}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()) // Espera status 200
                .andExpect(jsonPath("$[0].subjectName").value("Mathematics")) // Verifica o nome da matéria no JSON
                .andExpect(jsonPath("$[0].percentage").value(75.0)) // Verifica a dificuldade no JSON
                .andExpect(jsonPath("$[1].subjectName").value("Physics"))
                .andExpect(jsonPath("$[1].percentage").value(50.0))
                .andExpect(jsonPath("$[2].subjectName").value("Chemistry"))
                .andExpect(jsonPath("$[2].percentage").value(35.0));
    }

    @Test
    public void testGetSubjectDifficulties_NotFound() throws Exception {
        // Mock do serviço para lançar uma exceção quando o estudante não for encontrado
        when(performanceService.getSubjectDifficulties(eq(1L)))
                .thenThrow(new ResourceNotFoundException("Estudante não encontrado"));

        mockMvc.perform(get("/api/performance/difficulties/{studentId}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isNotFound()); // Espera status 404
    }

    @Test
    public void testCreateInitialPerformance_BusinessException_PerformanceIncompleta() throws Exception {
        // Mock do serviço para lançar uma exceção de negócio
        when(performanceService.createInitialPerformance(eq(1L), anyList()))
                .thenThrow(new BusinessException("Não tem performance inicial para todas as matérias."));

        // Corpo da requisição em formato JSON
        String requestBody = "[{\"subjectId\": 1, \"performaceValue\": 80.0}]";

        // Executa a requisição e verifica o resultado
        mockMvc.perform(post("/api/performance/initial-performance/{studentId}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)) // Define o corpo da requisição
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isNotFound()); // Espera status 400
    }

    @Test
    public void testCreateInitialPerformance_BusinessException_PerformanceNaoEncontrada() throws Exception {
        // Mock do serviço para lançar uma exceção de negócio
        when(performanceService.createInitialPerformance(eq(1L), anyList()))
                .thenThrow(new BusinessException("Performance não encontrada para o subjectId: 1"));

        // Corpo da requisição em formato JSON
        String requestBody = "[{\"subjectId\": 1, \"performaceValue\": 80.0}]";

        // Executa a requisição e verifica o resultado
        mockMvc.perform(post("/api/performance/initial-performance/{studentId}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)) // Define o corpo da requisição
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isNotFound()); // Espera status 400
    }
}
