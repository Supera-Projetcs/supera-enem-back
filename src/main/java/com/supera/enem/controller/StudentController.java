package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.Student.*;
import com.supera.enem.domain.Student;
import com.supera.enem.service.KeycloackUserService;
import com.supera.enem.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
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
    private StudentService studentService;

    @Autowired
    private KeycloackUserService keycloackUserService;


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

    @PutMapping("/{id}/email")
    public ResponseEntity<Void> updateEmail(@PathVariable Long id, @RequestBody UpdateEmailDTO dto) {
        System.out.println(dto);
        studentService.updateEmailStudent(id, dto.getEmail());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/username")
    public ResponseEntity<Void> updateUsername(@PathVariable Long id, @RequestBody UpdateUsernameDTO dto) {
        studentService.updateUsernameStudent(id, dto.getUsername());
        return ResponseEntity.ok().build();
    }
    @PutMapping("/password")
    public ResponseEntity<Void> updatePassword(HttpServletRequest request, @RequestBody UpdatePasswordDTO dto) {
        String authorizationHeader = request.getHeader("Authorization");
        String id = keycloackUserService.getKeycloakIdByToken(authorizationHeader);
        keycloackUserService.updatePassword(id, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }


}
