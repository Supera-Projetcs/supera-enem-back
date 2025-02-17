package com.supera.enem.service;

import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.controller.DTOS.Student.StudentDTO;
import com.supera.enem.controller.DTOS.Student.UpdateStudentDTO;
import com.supera.enem.controller.DTOS.StudentSubject.StudentSubjectRequestDTO;
import com.supera.enem.controller.DTOS.StudentSubject.StudentSubjectResponseDTO;
import com.supera.enem.controller.DTOS.SubjectDTO;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.domain.Address;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.StudentSubject;
import com.supera.enem.domain.Subject;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.exception.BusinessException;
import com.supera.enem.exception.ResourceAlreadyExists;
import com.supera.enem.mapper.StudentMapper;
import com.supera.enem.mapper.StudentSubjectMapper;
import com.supera.enem.mapper.UserKeycloakMapper;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.StudentSubjectRepository;
import com.supera.enem.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private StudentSubjectRepository studentSubjectRepository;

    @Mock
    private StudentSubjectMapper studentSubjectMapper;

    @Mock
    private KeycloackUserService keycloakService;

    @InjectMocks
    private StudentService studentService;

    @Mock
    private UserKeycloakMapper userKeycloakMapper;

    @Mock
    private StudentMapper studentMapper;

    private UseKeycloakRegistrationDTO keycloakDTO;
    private StudentDTO studentDTO;
    private Student student;
    private Student newStudent;

    private Subject subject1, subject2;
    private Student existingStudent;
    private String token;
    private String keycloakId;
    private StudentSubject studentSubject1, studentSubject2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = new Student();
        student.setId(1L);
        student.setEmail("old@example.com");
        student.setKeycloakId("keycloak-id-123");

        studentDTO = new StudentDTO();
        studentDTO.setEmail("test@example.com");
        studentDTO.setUsername("testUser");
        studentDTO.setPassword("StrongP@ss1");

        newStudent = new Student();
        newStudent.setEmail(studentDTO.getEmail());
        newStudent.setUsername(studentDTO.getUsername());

        keycloakDTO = new UseKeycloakRegistrationDTO();

        subject1 = new Subject();
        subject1.setId(1L);
        subject1.setName("Math");

        subject2 = new Subject();
        subject2.setId(2L);
        subject2.setName("Science");

        studentSubject1 = new StudentSubject();
        studentSubject1.setStudent(student);
        studentSubject1.setSubject(subject1);
        studentSubject1.setSubjectWeight(5.0);

        studentSubject2 = new StudentSubject();
        studentSubject2.setStudent(student);
        studentSubject2.setSubject(subject2);
        studentSubject2.setSubjectWeight(2.0);

        token = "valid-token";
        keycloakId = "keycloak-id-123";
        existingStudent = new Student();
        existingStudent.setId(1L);
        existingStudent.setKeycloakId(keycloakId);
        existingStudent.setFirstName("João");
        existingStudent.setLastName("Silva");
        existingStudent.setDreamCourse("Engenharia");
        existingStudent.setPhone("11999999999");
        existingStudent.setBirthDate(LocalDate.of(2000, 5, 15));
        existingStudent.setPreferredStudyDays(Set.of(Weekday.MONDAY, Weekday.WEDNESDAY));
        existingStudent.setAddress(new Address());

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

    @Test
    void shouldCreateStudentSuccessfully() {
        when(studentRepository.findByEmail(studentDTO.getEmail())).thenReturn(Optional.empty());
        when(studentRepository.findByUsername(studentDTO.getUsername())).thenReturn(Optional.empty());
        when(keycloakService.createUser(any())).thenReturn("keycloak-123");
        when(studentMapper.toStudent(studentDTO)).thenReturn(newStudent);
        when(studentRepository.save(newStudent)).thenReturn(newStudent);

        Student createdStudent = studentService.createStudent(studentDTO);

        assertNotNull(createdStudent);
        assertEquals(studentDTO.getEmail(), createdStudent.getEmail());
        assertEquals(studentDTO.getUsername(), createdStudent.getUsername());
        assertEquals("keycloak-123", createdStudent.getKeycloakId());

        verify(studentRepository, times(1)).save(newStudent);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(studentRepository.findByEmail(studentDTO.getEmail())).thenReturn(Optional.of(student));

        Exception exception = assertThrows(ResourceAlreadyExists.class, () -> {
            studentService.createStudent(studentDTO);
        });

        assertEquals("Estudante com este e-mail já existe.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(studentRepository.findByUsername(studentDTO.getUsername())).thenReturn(Optional.of(student));

        Exception exception = assertThrows(ResourceAlreadyExists.class, () -> {
            studentService.createStudent(studentDTO);
        });

        assertEquals("Estudante com este username já existe.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordIsTooShort() {
        studentDTO.setPassword("P@1");

        Exception exception = assertThrows(BusinessException.class, () -> {
            studentService.createStudent(studentDTO);
        });

        assertEquals("A senha deve ter pelo menos 3 caracteres.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordHasNoNumber() {
        studentDTO.setPassword("StrongPass!");

        Exception exception = assertThrows(BusinessException.class, () -> {
            studentService.createStudent(studentDTO);
        });

        assertEquals("A senha deve conter pelo menos 1 número.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordHasNoUpperCaseLetter() {
        studentDTO.setPassword("weakp@ss1");

        Exception exception = assertThrows(BusinessException.class, () -> {
            studentService.createStudent(studentDTO);
        });

        assertEquals("A senha deve conter pelo menos uma letra maiúscula.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPasswordHasNoSymbol() {
        studentDTO.setPassword("StrongPass1");

        Exception exception = assertThrows(BusinessException.class, () -> {
            studentService.createStudent(studentDTO);
        });

        assertEquals("A senha deve conter pelo menos um símbolo.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenKeycloakFailsToCreateUser() {
        when(studentRepository.findByEmail(studentDTO.getEmail())).thenReturn(Optional.empty());
        when(studentRepository.findByUsername(studentDTO.getUsername())).thenReturn(Optional.empty());
        when(userKeycloakMapper.toKeycloakDTO(studentDTO)).thenReturn(keycloakDTO);
        when(keycloakService.createUser(any())).thenReturn(null);

        Exception exception = assertThrows(BusinessException.class, () -> {
            studentService.createStudent(studentDTO);
        });

        assertEquals("Erro ao criar usuário no Keycloak.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenSubjectsAreMissing() {
        List<StudentSubjectRequestDTO> listDto = List.of(new StudentSubjectRequestDTO(1L, 5.0));
        when(subjectRepository.findAll()).thenReturn(List.of(subject1, subject2));

        BusinessException thrown = assertThrows(BusinessException.class, () ->
                studentService.createStudentSubjects(1L, listDto)
        );

        assertEquals("Falta matéria", thrown.getMessage());
    }

    @Test
    void shouldCreateStudentSubjectsSuccessfully() {
        // Arrange
        List<StudentSubjectRequestDTO> listDto = Arrays.asList(
                new StudentSubjectRequestDTO(1L, 5.0),
                new StudentSubjectRequestDTO(2L, 2.0)
        );

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(subjectRepository.findAll()).thenReturn(List.of(subject1, subject2));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject1));
        when(subjectRepository.findById(2L)).thenReturn(Optional.of(subject2));

        when(studentSubjectRepository.saveAll(anyList())).thenReturn(List.of(studentSubject1, studentSubject2));

        when(studentSubjectMapper.toDto(any(StudentSubject.class))).thenAnswer(invocation -> {
            StudentSubject studentSubject = invocation.getArgument(0);
            return new StudentSubjectResponseDTO(
                    studentSubject.getId(),
                    studentSubject.getSubjectWeight(),
                    new SubjectDTO(studentSubject.getSubject().getId(), studentSubject.getSubject().getName())
            );
        });

        // Act
        List<StudentSubjectResponseDTO> response = studentService.createStudentSubjects(1L, listDto);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).getSubject().getId());
        assertEquals("Math", response.get(0).getSubject().getName());
        assertEquals(5.0, response.get(0).getSubjectWeight());

        assertEquals(2L, response.get(1).getSubject().getId());
        assertEquals("Science", response.get(1).getSubject().getName());
        assertEquals(2.0, response.get(1).getSubjectWeight());

        verify(studentRepository).findById(1L);
        verify(subjectRepository, times(2)).findById(anyLong());
        verify(studentSubjectRepository).saveAll(anyList());
        verify(studentSubjectMapper, times(2)).toDto(any(StudentSubject.class));
    }

    @Test
    void testUpdateStudent() {
        // Create a DTO with updated details
        UpdateStudentDTO updateStudentDTO = new UpdateStudentDTO();
        updateStudentDTO.setFirstName("Jane");  // The updated first name
        updateStudentDTO.setLastName("Doe");
        updateStudentDTO.setDreamCourse("Medicine");
        updateStudentDTO.setPhone("987654321");
        updateStudentDTO.setBirthDate(LocalDate.of(2000, 1, 1));
        updateStudentDTO.setPreferredStudyDays(Set.of());

        // Mock behavior for getStudentById method
        when(studentRepository.findById(1L)).thenReturn(java.util.Optional.of(existingStudent));

        // Mock the updateStudentFromDTO method to apply changes to the existing student
        doNothing().when(studentMapper).updateStudentFromDTO(updateStudentDTO, existingStudent);

        // Log the state of the existing student before update
        System.out.println("Existing student before update: " + existingStudent);

        // Use ArgumentCaptor to capture the argument passed to the save method
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);

        // Mock save method (we don't care about the return value in this case)
        when(studentRepository.save(studentCaptor.capture())).thenReturn(existingStudent);

        // Call the updateStudent method
        Student updatedStudent = studentService.updateStudent(1L, updateStudentDTO);

        // Log the state of the existing student after update
        System.out.println("Existing student after update: " + existingStudent);

        // Verify that save was called
        verify(studentRepository, times(1)).save(any(Student.class));

        // Capture the updated student passed to save
        Student capturedStudent = studentCaptor.getValue();

        // Log the captured student for further debugging
        System.out.println("Captured student: " + capturedStudent);

        // Verify that the captured student has the updated values
        assertEquals("Jane", capturedStudent.getFirstName());
        assertEquals("Doe", capturedStudent.getLastName());
        assertEquals("Medicine", capturedStudent.getDreamCourse());
        assertEquals("987654321", capturedStudent.getPhone());
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFound() {
        // Arrange
        UpdateStudentDTO updateStudentDTO = new UpdateStudentDTO();
        updateStudentDTO.setFirstName("Novo Nome");

        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> studentService.updateStudent(1L, updateStudentDTO));

        verify(studentRepository).findById(1L);
        verify(studentMapper, never()).updateStudentFromDTO(any(), any());
        verify(studentRepository, never()).save(any());
    }

    @Test
    void testGetStudentLogged() {
        // Mock do retorno do serviço KeycloakService
        when(keycloakService.getKeycloakIdByToken(token)).thenReturn(keycloakId);

        // Mock do retorno do repositório StudentRepository
        when(studentRepository.findByKeycloakId(keycloakId)).thenReturn(existingStudent);

        // Chama o método
        Student student = studentService.getStudentLogged(token);

        // Verifica se o método keycloakService foi chamado corretamente
        verify(keycloakService, times(1)).getKeycloakIdByToken(token);

        // Verifica se o método findByKeycloakId foi chamado corretamente
        verify(studentRepository, times(1)).findByKeycloakId(keycloakId);

        // Verifica se o retorno do método é o esperado
        assertNotNull(student);
        assertEquals(existingStudent.getId(), student.getId());
        assertEquals(existingStudent.getFirstName(), student.getFirstName());
        assertEquals(existingStudent.getLastName(), student.getLastName());
        assertEquals(existingStudent.getDreamCourse(), student.getDreamCourse());
    }

    @Test
    void testGetStudentLoggedWhenStudentNotFound() {
        // Mock do retorno do serviço KeycloakService
        when(keycloakService.getKeycloakIdByToken(token)).thenReturn(keycloakId);

        // Mock do retorno do repositório StudentRepository para não encontrar o estudante
        when(studentRepository.findByKeycloakId(keycloakId)).thenReturn(null);

        // Chama o método
        Student student = studentService.getStudentLogged(token);

        // Verifica que o retorno é null (não encontrou o estudante)
        assertNull(student);
    }

}
