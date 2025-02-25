package com.supera.enem.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.controller.DTOS.Student.StudentDTO;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.service.KeycloackUserService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KeycloackUserService keycloakService;

    @Autowired
    private StudentRepository studentRepository;


    @Test
    public void testRegisterStudent() throws Exception {
        System.out.println("Usuários no banco: " + studentRepository.findAll().size());
        when(keycloakService.createUser(any(UseKeycloakRegistrationDTO.class)))
                .thenReturn("mocked-user-id");

        StudentDTO studentDTO = new StudentDTO();
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
}