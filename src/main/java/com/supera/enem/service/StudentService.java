package com.supera.enem.service;

import com.supera.enem.controller.DTOS.Student.*;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;

import com.supera.enem.domain.Student;
import com.supera.enem.exception.ResourceAlreadyExists;
import com.supera.enem.mapper.StudentMapper;

import com.supera.enem.mapper.UserKeycloakMapper;
import com.supera.enem.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;


@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private KeycloackUserService keycloakService;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private UserKeycloakMapper userKeycloakMapper;


    @Autowired
    private PerformanceService performanceService;


    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudante com o ID " + id + " não foi encontrado."));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }


    public Student updateEmailStudent(Long id, String newEmail) {
        Student student = getStudentById(id);

        if (newEmail == null || newEmail.isEmpty()) throw new IllegalArgumentException("E-mail não pode ser nulo.");

        if(!newEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("E-mail invalido.");

        Optional<Student> existingStudentWithEmail = studentRepository.findByEmail(newEmail);
        if (existingStudentWithEmail.isPresent() && !existingStudentWithEmail.get().getId().equals(id))
            throw new IllegalArgumentException("Este e-mail já está sendo usado por outro estudante.");

        keycloakService.updateEmail(student.getKeycloakId(), newEmail);
        student.setEmail(newEmail);
        return studentRepository.save(student);
    }

    public Student updateUsernameStudent(Long id, String username) {
        Student student = getStudentById(id);

        if (username == null || username.isEmpty()) throw new IllegalArgumentException("Username é obrigatório.");

        Optional<Student> existingStudentWithEmail = studentRepository.findByUsername(username);
        if (existingStudentWithEmail.isPresent() && !existingStudentWithEmail.get().getId().equals(id))
            throw new IllegalArgumentException("Este username já está sendo usado por outro estudante.");

        keycloakService.updateUsername(student.getKeycloakId(), username);

        student.setUsername(username);
        return studentRepository.save(student);
    }

    public Student updateStudent (Long id, UpdateStudentDTO studentDTO) {
        Student existingStudent = getStudentById(id);

        if (existingStudent == null) throw new IllegalArgumentException("Estudante não existe.");

        studentMapper.updateStudentFromDTO(studentDTO, existingStudent);

        return studentRepository.save(existingStudent);
    }


    public Student getStudentLogged(String token){
        String keycloakId = keycloakService.getKeycloakIdByToken(token);
        Student student = studentRepository.findByKeycloakId(keycloakId);

        return student;

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
        if (studentRepository.findByEmail(studentRecord.getEmail()).isPresent()) throw new ResourceAlreadyExists("Estudante com este e-mail já existe.");

        if (studentRepository.findByUsername(studentRecord.getUsername()).isPresent()) throw new ResourceAlreadyExists("Estudante com este username já existe.");

        if (!studentRecord.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new ResourceAlreadyExists("E-mail inválido.");
        System.out.println("TO AQUI1");
        validatePassword(studentRecord.getPassword());
        System.out.println("TO AQUI2");
        UseKeycloakRegistrationDTO userKeycloakRecord = userKeycloakMapper.toKeycloakDTO(studentRecord);
        String keycloakUserId = keycloakService.createUser(userKeycloakRecord);
        System.out.println("TO AQUI3");
        if (keycloakUserId == null) throw new ResourceAlreadyExists("Erro ao criar usuário no Keycloak.");

        Student student = studentMapper.toStudent(studentRecord);
        student.setKeycloakId(keycloakUserId);
        Student savedStudent = studentRepository.save(student);
        System.out.println("TO AQUI4");
        performanceService.createInitialPerformance(savedStudent.getId(),studentRecord.getInitialPerformaceList());
        System.out.println("TO AQUI5");
        return savedStudent;
    }

}
