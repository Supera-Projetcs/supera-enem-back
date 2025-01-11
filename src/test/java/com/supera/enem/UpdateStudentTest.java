package com.supera.enem;

import com.supera.enem.controller.DTOS.AddressDTO;
import com.supera.enem.controller.DTOS.Student.StudentDTO;
import com.supera.enem.domain.Address;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.enums.Weekday;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.service.KeycloackUserService;
import com.supera.enem.service.StudentService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UpdateStudentTest {
    @InjectMocks
    private StudentService studentService;

    @Mock
    private KeycloackUserService keycloakImplementation;

    @Mock
    private StudentRepository studentRepository;

    private Student validStudent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validStudent = new Student();
        validStudent.setUsername("user123");
        validStudent.setKeycloakId("keycloak123");
        validStudent.setFirstName("João");
        validStudent.setLastName("Silva");
        validStudent.setDreamCourse("Medicina");
        validStudent.setPhone("123456789");
        validStudent.setEmail("joao.silva@gmail.com");
        validStudent.setBirthDate(LocalDate.of(2000, 1, 1));
        validStudent.setPreferredStudyDays(Set.of(Weekday.MONDAY, Weekday.WEDNESDAY));
        Address address = new Address();
        address.setStreet("Rua 1");
        address.setCity("Cidade");
        address.setState("Estado");
        address.setZipCode("12345-678");
        address.setHouseNumber("10");
        validStudent.setAddress(address);
    }


    @Test
    public void shouldUpdateEmailSuccessfuly() {
        String newEmail = "novoemail@gmail.com";
        when(studentRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(studentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));


       Student updatedStudent =  studentService.updateEmailStudent(validStudent.getId(),newEmail);


        assertNotNull(updatedStudent);
        assertEquals(newEmail, updatedStudent.getEmail());
        verify(studentRepository, times(1)).save(any());
    }
}
