package com.supera.enem.controller;

import com.supera.enem.TestDataUtil;
import com.supera.enem.controller.DTOS.WeeklyReportDTO;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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

    @Autowired
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
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private ContentRepository contentRepository;
    private WeeklyReport weeklyReport;

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

}
