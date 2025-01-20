package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.Student.StudentDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/auth")

public class AuthController {
    @Autowired
    private StudentService studentService;

    @PostMapping("/register")
    public ResponseEntity<Student> createUser(@RequestBody @Valid StudentDTO student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }


    @GetMapping("/user-logged")
    public ResponseEntity<Student> getUserLogged(HttpServletRequest request){
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token não encontrado.");
        }

        Student student = studentService.getStudentLogged(authorizationHeader);
        return ResponseEntity.ok(student);
    }


}
