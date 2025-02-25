package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.controller.DTOS.AnswerResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.repository.*;
import com.supera.enem.service.AnswerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AnswerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    private List<AnswerResponseDTO> answers;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TestRepository testRepository;

    private Question question;
    private TestEntity test;
    private Student student;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ContentRepository contentRepository;

    @Transactional
    @BeforeEach
    public void setUp() {
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

        test = new TestEntity();
        test.setType(TestType.WEEKLY);
        test.setStudent(student);
        testRepository.save(test);

        Content content = new Content();
        content.setName("Math Content");
        content.setContent_weight(1.0);
        content.setQuestion_weight(1.0);
        contentRepository.save(content);

        question = new Question();
        question.setAnswer('A');
        question.setContents(Set.of(content));
        questionRepository.save(question);

//        AnswerResponseDTO answer1 = new AnswerResponseDTO();
//        answer1.setId(1L);
//        answer1.setCorrect(true);
//
//        AnswerResponseDTO answer2 = new AnswerResponseDTO();
//        answer2.setId(2L);
//        answer2.setCorrect(false);
//
//        answers = Arrays.asList(answer1, answer2);
    }

    private Jwt mockJwt() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        return jwt;
    }

    @Test
    public void testCreateAnswer_Success() throws Exception {
        Jwt jwt = mockJwt();
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        AnswerRequestDTO answerRequest = new AnswerRequestDTO();
        answerRequest.setQuestionId(question.getId());
        answerRequest.setTestId(test.getId());
        answerRequest.setText('A');

        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true));
    }

    @Test
    public void testCreateAnswer_QuestionNotFound() throws Exception {
        Jwt jwt = mockJwt();
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        AnswerRequestDTO answerRequest = new AnswerRequestDTO();
        answerRequest.setQuestionId(999L); // ID inexistente
        answerRequest.setTestId(test.getId());
        answerRequest.setText('C');

        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testCreateAnswer_AlreadyAnswered() throws Exception {
        Jwt jwt = mockJwt();
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        Answer answer = new Answer();
        answer.setQuestion(question);
        answer.setTestEntity(test);
        answerRepository.save(answer);

        AnswerRequestDTO answerRequest = new AnswerRequestDTO();
        answerRequest.setQuestionId(question.getId());
        answerRequest.setTestId(test.getId());
        answerRequest.setText('C');

        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateAnswer_UserNotRelatedToTest() throws Exception {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("anotherKeycloakId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        AnswerRequestDTO answerRequest = new AnswerRequestDTO();
        answerRequest.setQuestionId(question.getId());
        answerRequest.setTestId(test.getId());
        answerRequest.setText('A');

        mockMvc.perform(post("/api/answers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(answerRequest)))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser
    public void testGetAnswersByTest_Success() throws Exception {
        // Mock do JwtDecoder para retornar um JWT válido
        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId"); // Simula o subject do JWT
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        // Configura o SecurityContext para usar o JWT
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        Long testId = 1L;

        // Mock do repositório para retornar um teste
        TestEntity test = new TestEntity();
        test.setId(testId);
        test.setStudent(student); // Associa o estudante ao teste
//        when(testRepository.findById(testId)).thenReturn(Optional.of(test));

        // Mock do repositório para retornar uma lista de respostas
        List<Answer> answers = Arrays.asList(new Answer(), new Answer());
//        when(answerRepository.findAll()).thenReturn(answers);

        mockMvc.perform(get("/api/answers/{testId}", testId)
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }
}
