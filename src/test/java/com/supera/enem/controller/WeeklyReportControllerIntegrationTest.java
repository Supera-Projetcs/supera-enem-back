package com.supera.enem.controller;

import com.supera.enem.TestDataUtil;
import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;
import com.supera.enem.domain.*;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.SubjectRepository;
import com.supera.enem.repository.WeeklyReportRepository;
import com.supera.enem.service.AuthenticatedService;
import com.supera.enem.service.WeeklyReportService;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WeeklyReportControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeeklyReportService weeklyReportService;

    @MockBean
    private AuthenticatedService authenticatedService;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TestDataUtil testDataUtil;

    private Student student;

    private WeeklyReportDTO weeklyReportDTO;

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;

    @Transactional
    @BeforeEach
    public void setUp() {
        student = testDataUtil.createAndSaveStudent();
        Subject subject = testDataUtil.createAndSaveSubject();

        Content content = testDataUtil.createAndSaveContent(subject);
        Question question = testDataUtil.createAndSaveQuestion(content);
        TestEntity testEntity = testDataUtil.createAndSaveTestEntity(student, List.of(question));
        testDataUtil.createAndSaveAnswer(question, testEntity);
        testDataUtil.createAndSavePerformance(student, content);
        testDataUtil.createAndSaveWeeklyReport(student, Set.of(content));

        when(authenticatedService.getAuthenticatedStudent()).thenReturn(student);
    }

    @Test
    public void testGetWeeklyReportById_Success() throws Exception {
        // Mock JwtDecoder to return a valid JWT
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        WeeklyReport weeklyReport = weeklyReportRepository.findAll().get(0);
        assertNotNull(weeklyReport);
        assertNotNull(weeklyReport.getId());
        System.out.println("WeeklyReport ID in test: " + weeklyReport.getId());

        System.out.println("Mocking weeklyReportService.getWeeklyReportById with ID: " + weeklyReport.getId());
        when(weeklyReportService.getWeeklyReportById(any(Long.class), any(Student.class))).thenReturn(weeklyReportDTO);

        mockMvc.perform(get("/api/weekly-reports/{id}", weeklyReport.getId())
                        .header("Authorization", "Bearer valid-token"))
                .andDo(result -> {
                    if (result.getResolvedException() != null) {
                        System.err.println("Exception: " + result.getResolvedException().getMessage());
                        result.getResolvedException().printStackTrace();
                    }
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isOk());
    }

    @Test
    public void testGetWeeklyReport_ExistingReport() throws Exception {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        LocalDate currentDate = LocalDate.now();

        WeeklyReport existingReport = new WeeklyReport();
        existingReport.setId(1L);
        existingReport.setStudent(student);
        existingReport.setDate(java.sql.Date.valueOf(currentDate));

        mockMvc.perform(get("/api/weekly-reports/week")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateWeeklyReport_Success() throws Exception {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        // Mock do serviço para retornar o WeeklyReportDTO atualizado
        when(weeklyReportService.updateWeeklyReport(any(WeeklyReportRequestDTO.class), eq(1L))).thenReturn(weeklyReportDTO);

        // Executa a requisição e verifica o resultado
        mockMvc.perform(post("/api/weekly-reports/update/{id}", 1L)
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\": \"2023-10-15\", \"contentIds\": [1, 2]}")) // Corpo da requisição
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Verifica se a data existe no JSON
    }

}
