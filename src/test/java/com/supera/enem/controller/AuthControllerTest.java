package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.Student.StudentDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.exception.ResourceAlreadyExists;
import com.supera.enem.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    private StudentDTO studentDTO;

    @BeforeEach
    public void setUp() {
        studentDTO = new StudentDTO();
        studentDTO.setEmail("test@example.com");
        studentDTO.setUsername("testuser");
        studentDTO.setPassword("TestPassword123");
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setEmail(studentDTO.getEmail());
        student.setUsername(studentDTO.getUsername());

        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(student);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(studentDTO.getEmail()))
                .andExpect(jsonPath("$.username").value(studentDTO.getUsername()));

        verify(studentService, times(1)).createStudent(any(StudentDTO.class));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() throws Exception {
        when(studentService.createStudent(any(StudentDTO.class)))
                .thenThrow(new ResourceAlreadyExists("Estudante com este e-mail já existe."));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Estudante com este e-mail já existe."));

        verify(studentService, times(1)).createStudent(any(StudentDTO.class));
    }

    @Test
    public void testCreateUser_InvalidEmail() throws Exception {
        studentDTO.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("E-mail inválido."));

        verify(studentService, never()).createStudent(any(StudentDTO.class));
    }
}
