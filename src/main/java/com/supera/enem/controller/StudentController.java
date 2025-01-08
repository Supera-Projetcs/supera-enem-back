package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.UpdateStudentDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api/students")

public class StudentController {
    @Autowired
    private  StudentService studentService;


    @GetMapping("/")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Student> partialUpdateStudent(
            @PathVariable Long id,
            @RequestBody @Valid UpdateStudentDTO studentDTO) {


        Student updatedStudent = studentService.updateStudent(id, studentDTO );

        return ResponseEntity.status(HttpStatus.OK).body(updatedStudent);
    }

    @GetMapping("/{id}/")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }
}
