package com.supera.enem.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.controller.DTOS.Student.StudentRequestDTO;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.service.KeycloackUserService;
import com.supera.enem.service.StudentService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KeycloackUserService keycloakService;

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private StudentService studentService;

    private Student student;

    @BeforeEach
    public void setUp() {
        student = new Student();
        student.setId(1L);
        student.setEmail("test@example.com");
        student.setUsername("testuser");
        student.setKeycloakId("keycloakUserId");
    }

    @Test
    public void testRegisterStudent() throws Exception {
        System.out.println("Usuários no banco: " + studentRepository.findAll().size());
        when(keycloakService.createUser(any(UseKeycloakRegistrationDTO.class)))
                .thenReturn("mocked-user-id");

        StudentRequestDTO studentDTO = new StudentRequestDTO();
        studentDTO.setUsername("latransa");
        studentDTO.setFirstName("string");
        studentDTO.setLastName("string");
        studentDTO.setDreamCourse("string");
        studentDTO.setPhone("string");
        studentDTO.setEmail("emailtop@gmail.com");
        studentDTO.setBirthDate(LocalDate.of(2025, 2, 24));

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("string");
        addressDTO.setCity("string");
        addressDTO.setState("string");
        addressDTO.setZipCode("string");
        addressDTO.setHouseNumber("string");

        studentDTO.setAddress(addressDTO);
        studentDTO.setPassword("Tiolu666$");
        studentDTO.setPreferredStudyDays(Set.of(Weekday.MONDAY));

        String studentJson = objectMapper.writeValueAsString(studentDTO);

        when(keycloakService.createUser(any(UseKeycloakRegistrationDTO.class)))
                .thenReturn("mocked-user-id");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);

        assertThat(result.getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    public void testGetUserLogged_Success() throws Exception {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);

        when(studentRepository.findByKeycloakId("keycloakUserId")).thenReturn(student);

        mockMvc.perform(get("/api/auth/user-logged")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUserLogged_InvalidToken() throws Exception {
        mockMvc.perform(get("/user-logged"))
                .andExpect(status().isUnauthorized());

        verify(keycloakService, never()).getKeycloakIdByToken(anyString());
        verify(studentRepository, never()).findByKeycloakId(anyString());
    }

}