package com.supera.enem.service;

import com.supera.enem.domain.Student;
import com.supera.enem.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private KeycloackUserService keycloakService;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = new Student();
        student.setId(1L);
        student.setEmail("old@example.com");
        student.setKeycloakId("keycloak-id-123");
    }

    @Test
    void shouldUpdateEmailSuccessfully() {
        String newEmail = "new@example.com";

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student updatedStudent = studentService.updateEmailStudent(1L, newEmail);

        assertNotNull(updatedStudent);
        assertEquals(newEmail, updatedStudent.getEmail());
        verify(keycloakService).updateEmail(student.getKeycloakId(), newEmail);
        verify(studentRepository).save(student);
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        Student student = new Student();
        student.setId(1L);
        student.setEmail("valid@email.com");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.updateEmailStudent(1L, null);
        });
        assertEquals("E-mail não pode ser nulo.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsInvalid() {
        String invalidEmail = "invalid-email";

        Student student = new Student();
        student.setId(1L);
        student.setEmail("valid@email.com");
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.updateEmailStudent(1L, invalidEmail);
        });

        assertEquals("E-mail invalido.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailIsAlreadyInUse() {
        String existingEmail = "existing@example.com";
        Student anotherStudent = new Student();
        anotherStudent.setId(2L);
        anotherStudent.setEmail(existingEmail);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findByEmail(existingEmail)).thenReturn(Optional.of(anotherStudent));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.updateEmailStudent(1L, existingEmail);
        });
        assertEquals("Este e-mail já está sendo usado por outro estudante.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsNull() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.updateUsernameStudent(1L, null);
        });

        assertEquals("Username é obrigatório.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameIsEmpty() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.updateUsernameStudent(1L, "");
        });

        assertEquals("Username é obrigatório.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        Student anotherStudent = new Student();
        anotherStudent.setId(2L);
        anotherStudent.setUsername("newUsername");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findByUsername("newUsername")).thenReturn(Optional.of(anotherStudent));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            studentService.updateUsernameStudent(1L, "newUsername");
        });

        assertEquals("Este username já está sendo usado por outro estudante.", exception.getMessage());
    }

    @Test
    void shouldUpdateUsernameSuccessfully() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.findByUsername("newUsername")).thenReturn(Optional.empty());

        doNothing().when(keycloakService).updateUsername("keycloak-123", "newUsername");
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student updatedStudent = studentService.updateUsernameStudent(1L, "newUsername");

        assertNotNull(updatedStudent);
        assertEquals("newUsername", updatedStudent.getUsername());
        verify(keycloakService, times(1)).updateUsername("keycloak-id-123", "newUsername");
        verify(studentRepository, times(1)).save(student);
    }

}
