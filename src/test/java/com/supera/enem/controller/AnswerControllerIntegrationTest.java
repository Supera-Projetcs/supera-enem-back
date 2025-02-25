package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.domain.Answer;
import com.supera.enem.domain.Question;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.TestEntity;
import com.supera.enem.domain.enums.TestType;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.repository.AnswerRepository;
import com.supera.enem.repository.QuestionRepository;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.TestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

        question = new Question();
        question.setAnswer('A');
        questionRepository.save(question);
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
                .andExpect(status().isConflict());
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

}
