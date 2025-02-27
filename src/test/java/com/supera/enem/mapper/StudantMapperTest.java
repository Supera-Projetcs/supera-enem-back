package com.supera.enem.mapper;

import com.supera.enem.controller.DTOS.Student.StudentRequestDTO;
import com.supera.enem.controller.DTOS.Student.UpdateStudentDTO;
import com.supera.enem.domain.Student;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StudantMapperTest {

    private final StudentMapper studentMapper = Mappers.getMapper(StudentMapper.class);

    @Test
    public void testToStudent() {
        StudentRequestDTO dto = new StudentRequestDTO();
        dto.setUsername("test_user");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setDreamCourse("Computer Science");
        dto.setPhone("123456789");
        dto.setEmail("john.doe@example.com");
        dto.setBirthDate(LocalDate.of(2000, 1, 1));

        Student student = studentMapper.toStudent(dto);

        assertNotNull(student);
        assertEquals(dto.getUsername(), student.getUsername());
        assertEquals(dto.getFirstName(), student.getFirstName());
        assertEquals(dto.getLastName(), student.getLastName());
        assertEquals(dto.getDreamCourse(), student.getDreamCourse());
        assertEquals(dto.getPhone(), student.getPhone());
        assertEquals(dto.getEmail(), student.getEmail());
        assertEquals(dto.getBirthDate(), student.getBirthDate());
    }

    @Test
    public void testUpdateStudentFromDTO() {
        Student student = new Student();
        student.setId(1L);
        student.setUsername("old_user");
        student.setFirstName("OldFirstName");
        student.setLastName("OldLastName");

        UpdateStudentDTO updateDTO = new UpdateStudentDTO();
        updateDTO.setFirstName("NewFirstName");
        updateDTO.setLastName("NewLastName");

        studentMapper.updateStudentFromDTO(updateDTO, student);

        assertEquals("NewFirstName", student.getFirstName());
        assertEquals("NewLastName", student.getLastName());
        assertEquals("old_user", student.getUsername()); // Não deve ser alterado
        assertNotNull(student.getId()); // Deve continuar com o ID original
    }

}
