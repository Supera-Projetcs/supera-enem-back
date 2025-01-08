package com.supera.enem.service;

import com.supera.enem.controller.DTOS.StudentDTO;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.domain.Student;
import com.supera.enem.mapper.StudentMapper;

import com.supera.enem.mapper.UserKeycloakMapper;
import com.supera.enem.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final KeycloackUserService keycloakImplemantation;
    private final StudentMapper studentMapper;
    private final UserKeycloakMapper userKeycloakMapper;

    public StudentService(StudentRepository studentRepository, KeycloackUserService keycloakImplemantation) {
        this.studentRepository = studentRepository;
        this.keycloakImplemantation = keycloakImplemantation;
        this.studentMapper = StudentMapper.INSTANCE;
        this.userKeycloakMapper = UserKeycloakMapper.INSTANCE;
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudante com o ID " + id + " não foi encontrado."));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    private void validatePassword(String password) {
        if (password.length() < 3) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 3 caracteres.");
        }
        if (!password.matches(".*\\d.*")) { // Verifica se contém pelo menos um número
            throw new IllegalArgumentException("A senha deve conter pelo menos 1 número.");
        }
        if (!password.matches(".*[A-Z].*")) { // Verifica se contém pelo menos uma letra maiúscula
            throw new IllegalArgumentException("A senha deve conter pelo menos uma letra maiúscula.");
        }
        if (!password.matches(".*[@#!$%^&*].*")) { // Verifica se contém pelo menos um símbolo
            throw new IllegalArgumentException("A senha deve conter pelo menos um símbolo.");
        }
    }

    public Student createStudent(StudentDTO studentRecord) {
        if (studentRepository.findByEmail(studentRecord.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Estudante com este e-mail já existe.");
        }

        if (studentRepository.findByUsername(studentRecord.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Estudante com este username já existe.");
        }

        if (!studentRecord.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }

        validatePassword(studentRecord.getPassword());

        UseKeycloakRegistrationDTO userKeycloakRecord = userKeycloakMapper.toKeycloakDTO(studentRecord);
        String keycloakUserId = keycloakImplemantation.createUser(userKeycloakRecord);


        if (keycloakUserId == null) {
            throw new IllegalStateException("Erro ao criar usuário no Keycloak.");
        }

        Student student = studentMapper.toStudent(studentRecord);
        student.setKeycloakId(keycloakUserId);
        return studentRepository.save(student);
    }

}
