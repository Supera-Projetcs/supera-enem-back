package com.supera.enem.controller;
//
//import com.supera.enem.domain.Answer;
//import com.supera.enem.domain.Question;
//import com.supera.enem.domain.Student;
//import com.supera.enem.domain.TestEntity;
//import com.supera.enem.domain.enums.TestType;
//import com.supera.enem.repository.AnswerRepository;
//import com.supera.enem.repository.QuestionRepository;
//import com.supera.enem.repository.StudentRepository;
//import com.supera.enem.repository.TestRepository;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Collections;
//import java.util.Date;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@RunWith(SpringRunner.class) // JUnit 4 runner
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test") // Ensures it uses application-test.yml
public class AnswerControllerIT {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private AnswerRepository answerRepository;
//
//    @Autowired
//    private QuestionRepository questionRepository;
//
//    @Autowired
//    private StudentRepository studentRepository;
//
//    @Autowired
//    private TestRepository testRepository;
//
//    @Mock
//    private JwtDecoder jwtDecoder; // Mocking Keycloak JWT Decoder
//
//    private TestEntity testEntity;
//    private String keycloakId = "mock-user-id"; // Simulated Keycloak user ID
//
//    @Before
//    public void setUp() {
//        // Create and save a Student
//        Student student = new Student();
//        student.setKeycloakId(keycloakId); // Ensuring the JWT user matches
//        student = studentRepository.save(student);
//
//        // Create and save a TestEntity
//        testEntity = new TestEntity();
//        testEntity.setType(TestType.WEEKLY);
//        testEntity.setDate(new Date());
//        testEntity.setStudent(student);
//        testEntity.setQuestions(Collections.emptyList()); // Empty questions for simplicity
//        testEntity = testRepository.save(testEntity);
//
//        // Create and save a Question
//        Question question = new Question();
//        question.setText("What is the capital of France?");
//        question = questionRepository.save(question);
//
//        // Create and save an Answer
//        Answer answer = new Answer();
//        answer.setText('A'); // Assuming 'A' is a valid answer choice
//        answer.setCorrect(true);
//        answer.setQuestion(question);
//        answer.setTestEntity(testEntity);
//        answerRepository.save(answer);
//
//        // Mock Keycloak JWT Authentication
//        Jwt jwt = Jwt.withTokenValue("mock-token")
//                .header("alg", "none")
//                .claim("sub", keycloakId) // Keycloak user ID
//                .build();
//        when(jwtDecoder.decode("mock-token")).thenReturn(jwt);
//    }
//
////    @Test
////    public void shouldReturnAnswersByTest() throws Exception {
////        mockMvc.perform(get("/api/answers/" + testEntity.getId())
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .with(jwt().jwt(jwtDecoder.decode("mock-token")))) // Simulate Keycloak Authentication
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.size()").value(1))
////                .andExpect(jsonPath("$[0].text").value("A"));
////
////        // Assert that an answer was saved
////        assertThat(answerRepository.findAll()).hasSize(1);
////    }
}
