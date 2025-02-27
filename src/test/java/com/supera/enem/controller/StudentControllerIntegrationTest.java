package com.supera.enem.controller;


import com.supera.enem.TestDataUtil;
import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.controller.DTOS.Student.UpdateEmailDTO;
import com.supera.enem.controller.DTOS.Student.UpdatePasswordDTO;
import com.supera.enem.controller.DTOS.Student.UpdateStudentDTO;
import com.supera.enem.controller.DTOS.Student.UpdateUsernameDTO;
import com.supera.enem.controller.DTOS.StudentSubject.StudentSubjectRequestDTO;
import com.supera.enem.controller.DTOS.StudentSubject.StudentSubjectResponseDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.Subject;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.service.KeycloackUserService;
import com.supera.enem.service.StudentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @MockBean
    private KeycloackUserService keycloakUserService;

    @Autowired
    private TestDataUtil testDataUtil;

    @MockBean
    private JwtDecoder jwtDecoder;


    private Student student;
    private Subject subject1;
    private Subject subject2;

    @Transactional
    @BeforeEach
    public void setUp() {
        // Cria e salva um estudante no banco de dados de teste
        student = testDataUtil.createAndSaveStudent();
        subject1 = testDataUtil.createAndSaveSubject();
        subject2 = testDataUtil.createAndSaveSubject();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaim("sub")).thenReturn("keycloakUserId");
        when(jwtDecoder.decode("valid-token")).thenReturn(jwt);
    }

    @Test
    public void testPartialUpdateStudent_Success() throws Exception {
        // Cria um DTO de atualização
        UpdateStudentDTO updateStudentDTO = new UpdateStudentDTO();
        updateStudentDTO.setName("John Doe");

        updateStudentDTO.setDreamCourse("Computer Science");
        updateStudentDTO.setPhone("1234567890");
        updateStudentDTO.setBirthDate(LocalDate.of(2000, 1, 1));

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("123 Main St");
        addressDTO.setNeighborhood("Bairro");
        addressDTO.setCity("Springfield");
        addressDTO.setState("IL");
        addressDTO.setZipCode("62701");
        updateStudentDTO.setAddress(addressDTO);

        updateStudentDTO.setPreferredStudyDays(Set.of(Weekday.MONDAY, Weekday.WEDNESDAY));

        // Mock do serviço para retornar o estudante atualizado
        when(studentService.updateStudent(eq(student.getId()), any(UpdateStudentDTO.class)))
                .thenReturn(student);

        // Executa a requisição e verifica o resultado
        mockMvc.perform(patch("/api/students/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content("{\n" +
                                "  \"name\": \"John Doe\",\n" +
                                "  \"dreamCourse\": \"Computer Science\",\n" +
                                "  \"phone\": \"1234567890\",\n" +
                                "  \"birthDate\": \"2000-01-01\",\n" +
                                "  \"address\": {\n" +
                                "    \"street\": \"123 Main St\",\n" +
                                "    \"city\": \"Springfield\",\n" +
                                "    \"neighborhood\": \"Bairro\",\n" +
                                "    \"state\": \"IL\",\n" +
                                "    \"zipCode\": \"62701\"\n" +
                                "  },\n" +
                                "  \"preferredStudyDays\": [\"MONDAY\", \"WEDNESDAY\"]\n" +
                                "}"))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Espera status 200
    }


    @Test
    public void testUpdateEmail_Success() throws Exception {
        // Cria um DTO de atualização de email
        UpdateEmailDTO updateEmailDTO = new UpdateEmailDTO();
        updateEmailDTO.setEmail("newEmail@example.com");

        // Mock do serviço para retornar o estudante atualizado
        when(studentService.updateEmailStudent(eq(student.getId()), eq("newEmail@example.com")))
                .thenReturn(student);

        // Executa a requisição e verifica o resultado
        mockMvc.perform(patch("/api/students/{id}/email", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content("{\"email\": \"newEmail@example.com\"}"))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Espera status 200
    }

    @Test
    public void testUpdateUsername_Success() throws Exception {
        // Cria um DTO de atualização de username
        UpdateUsernameDTO updateUsernameDTO = new UpdateUsernameDTO();
        updateUsernameDTO.setUsername("newUsername");

        mockMvc.perform(put("/api/students/{id}/username", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content("{\"username\": \"newUsername\"}"))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Espera status 200
    }

    @Test
    public void testUpdatePassword_Success() throws Exception {
        // Cria um DTO de atualização de senha
        UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO();
        updatePasswordDTO.setNewPassword("newPassword");

        // Mock do serviço para atualizar a senha no Keycloak
        doNothing().when(keycloakUserService).updatePassword(eq("keycloakUserId"), eq(updatePasswordDTO));

        // Mock do JWT para retornar um ID do Keycloak
        when(keycloakUserService.getKeycloakIdByToken("valid-token")).thenReturn("keycloakUserId");

        // Executa a requisição e verifica o resultado
        mockMvc.perform(put("/api/students/password")
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content("{\"currentPassword\": \"oldPassword\", \"newPassword\": \"newPassword\"}"))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Espera status 200
    }

    @Test
    public void testGetStudentById_Success() throws Exception {
        // Mock do serviço para retornar o estudante
        when(studentService.getStudentById(student.getId())).thenReturn(student);

        // Executa a requisição e verifica o resultado
        mockMvc.perform(get("/api/students/{id}/", student.getId())
                        .header("Authorization", "Bearer valid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()) // Imprime detalhes da requisição e da resposta
                .andExpect(status().isOk()); // Espera status 200
    }

//    @Test
//    public void testCreateStudentSubject_Success() throws Exception {
//        // Cria uma lista de DTOs de matérias do estudante
//        List<StudentSubjectRequestDTO> studentSubjectRequestDTOList = List.of(
//                new StudentSubjectRequestDTO(1L, 0.5),
//                new StudentSubjectRequestDTO(2L, 0.7)
//        );
//
//        // Mock do serviço para retornar a lista de matérias criadas
//        List<StudentSubjectResponseDTO> studentSubjectResponseDTOList = List.of(
//                new StudentSubjectResponseDTO(1L, 0.5, subject1),
//                new StudentSubjectResponseDTO(2L, 0.7, subject2)
//        );
//        when(studentService.createStudentSubjects(eq(student.getId()), eq(studentSubjectRequestDTOList)))
//                .thenReturn(studentSubjectResponseDTOList);
//
//        // Executa a requisição e verifica o resultado
//        mockMvc.perform(post("/api/students/subject-weight/{id}", student.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("[{\"subjectId\": 1, \"weight\": 0.5}, {\"subjectId\": 2, \"weight\": 0.7}]"))
//                .andDo(print()) // Imprime detalhes da requisição e da resposta
//                .andExpect(status().isOk()); // Espera status 200
//    }

}
