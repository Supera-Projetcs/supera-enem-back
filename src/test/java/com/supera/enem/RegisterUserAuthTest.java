package com.supera.enem;

import com.supera.enem.controller.DTOS.StudentDTO;
import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.service.StudentService;
import com.supera.enem.service.KeycloackUserService;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterUserAuthTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private KeycloackUserService keycloakImplementation;

    @InjectMocks
    private StudentService studentService;

    private StudentDTO validStudentDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup de um DTO válido
        validStudentDTO = new StudentDTO();
        validStudentDTO.setUsername("user123");
        validStudentDTO.setFirstName("João");
        validStudentDTO.setLastName("Silva");
        validStudentDTO.setDreamCourse("Medicina");
        validStudentDTO.setPhone("123456789");
        validStudentDTO.setEmail("joao.silva@gmail.com");
        validStudentDTO.setBirthDate(LocalDate.of(2000, 1, 1));
        validStudentDTO.setPassword("Senha123@");
        validStudentDTO.setPreferredStudyDays(Set.of(Weekday.MONDAY, Weekday.WEDNESDAY));
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("Rua 1");
        addressDTO.setCity("Cidade");
        addressDTO.setState("Estado");
        addressDTO.setZipCode("12345-678");
        addressDTO.setHouseNumber("10");
        validStudentDTO.setAddress(addressDTO);
    }

    @Test
    void shouldRegisterStudentSuccessfully() {

        when(studentRepository.findByEmail(validStudentDTO.getEmail())).thenReturn(Optional.empty());
        when(studentRepository.findByUsername(validStudentDTO.getUsername())).thenReturn(Optional.empty());
        when(keycloakImplementation.createUser(any())).thenReturn("keycloakId123");
        when(studentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));


        Student createdStudent = studentService.createStudent(validStudentDTO);


        assertNotNull(createdStudent);
        assertEquals("keycloakId123", createdStudent.getKeycloakId());
        assertEquals(validStudentDTO.getFirstName(), createdStudent.getFirstName());
        verify(studentRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        when(studentRepository.findByEmail(validStudentDTO.getEmail()))
                .thenReturn(Optional.of(new Student()));


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.createStudent(validStudentDTO)
        );
        assertEquals("Estudante com este e-mail já existe.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordTooShort() {
        validStudentDTO.setPassword("ab");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.createStudent(validStudentDTO)
        );
        assertEquals("A senha deve ter pelo menos 3 caracteres.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordHasNoNumber() {
        validStudentDTO.setPassword("Senha@");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.createStudent(validStudentDTO)
        );
        assertEquals("A senha deve conter pelo menos 1 número.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordHasNoUppercaseLetter() {
        validStudentDTO.setPassword("senha@1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.createStudent(validStudentDTO)
        );
        assertEquals("A senha deve conter pelo menos uma letra maiúscula.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordHasNoSymbol() {
        validStudentDTO.setPassword("Senha1");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.createStudent(validStudentDTO)
        );
        assertEquals("A senha deve conter pelo menos um símbolo.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(studentRepository.findByUsername(validStudentDTO.getUsername()))
                .thenReturn(Optional.of(new Student()));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.createStudent(validStudentDTO)
        );
        assertEquals("Estudante com este username já existe.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        validStudentDTO.setEmail("emailsemarroba.com");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                studentService.createStudent(validStudentDTO)
        );
        assertEquals("E-mail inválido.", exception.getMessage());
    }
}
