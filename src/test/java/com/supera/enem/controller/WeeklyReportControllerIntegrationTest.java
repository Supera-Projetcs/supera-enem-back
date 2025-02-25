package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.repository.StudentRepository;
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
import java.util.Optional;
import java.util.Set;

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
//    @MockBean
    private WeeklyReportService weeklyReportService;

    @MockBean
    private AuthenticatedService authenticatedService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private Student student;
    private WeeklyReportDTO weeklyReportDTO;
    @Autowired
    private WeeklyReportRepository weeklyReportRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    @BeforeEach
    public void setUp() {
//        student = Instancio.create(Student.class);
        student = new Student();
        student.setEmail("test@example.com");
        student.setUsername("testuser");
        student.setKeycloakId("keycloakUserId");
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        student.setBirthDate(birthDate);
        student.setDreamCourse("Computer Science");
        student.setPhone("123456789");
        student.setFirstName("Test");
        student.setLastName("User");
        Set<Weekday> weekdays = Set.of(Weekday.MONDAY, Weekday.WEDNESDAY, Weekday.FRIDAY);
        student.setPreferredStudyDays(weekdays);
        studentRepository.save(student);

        student = studentRepository.save(student);

        when(authenticatedService.getAuthenticatedStudent()).thenReturn(student);

        WeeklyReport weeklyReport = new WeeklyReport();
        Date today = new Date();
        today.setTime(today.getTime() - 1000 * 60 * 60 * 24 * 7);
        weeklyReport.setDate(today);
        weeklyReport.setStudent(student);
        weeklyReportRepository.save(weeklyReport);
    }


    @Test
    public void testGetWeeklyReportById_Success() throws Exception {
        // Mock do JwtDecoder para retornar um JWT válido
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        WeeklyReport weeklyReport = weeklyReportRepository.findByStudent(student).get(0);

        // Mock do AuthenticatedService para retornar o estudante autenticado
        when(authenticatedService.getAuthenticatedStudent()).thenReturn(student);
        when(weeklyReportService.getWeeklyReportById(weeklyReport.getId(), student)).thenReturn(weeklyReportDTO);

        // Executa a requisição e verifica o resultado
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
    public void testGetWeeklyReportById_NotFound() throws Exception {
        Long reportId = 2L;

        // Mock the JwtDecoder to return a valid JWT
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        // Mock the authenticated student
        when(authenticatedService.getAuthenticatedStudent()).thenReturn(student);

        // Perform the request with the Authorization header
        mockMvc.perform(get("/api/weekly-reports/{id}", reportId)
                        .header("Authorization", "Bearer valid-token"))
                .andDo(result -> {
                    if (result.getResolvedException() != null) {
                        System.err.println("Exception: " + result.getResolvedException().getMessage());
                        result.getResolvedException().printStackTrace();
                    }
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isNotFound());
    }
}
