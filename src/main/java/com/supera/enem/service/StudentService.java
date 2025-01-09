package com.supera.enem.service;

import com.supera.enem.controller.DTOS.Student.*;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;

import com.supera.enem.domain.Student;
import com.supera.enem.mapper.StudentMapper;

import com.supera.enem.mapper.UserKeycloakMapper;
import com.supera.enem.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;


@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final KeycloackUserService keycloakService;
    private final StudentMapper studentMapper;
    private final UserKeycloakMapper userKeycloakMapper;

    public StudentService(StudentRepository studentRepository, KeycloackUserService keycloakImplemantation) {
        this.studentRepository = studentRepository;
        this.keycloakService = keycloakImplemantation;
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


    public void updateEmailStudent(Long id, String newEmail) {
        Student student = getStudentById(id);

        if (newEmail == null || newEmail.isEmpty()) throw new IllegalArgumentException("E-mail não pode ser nulo.");

        if(!newEmail.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("E-mail invalido.");

        Optional<Student> existingStudentWithEmail = studentRepository.findByEmail(newEmail);
        if (existingStudentWithEmail.isPresent() && !existingStudentWithEmail.get().getId().equals(id))
            throw new IllegalArgumentException("Este e-mail já está sendo usado por outro estudante.");

        keycloakService.updateEmail(student.getKeycloakId(), newEmail);
        student.setEmail(newEmail);
        studentRepository.save(student);
    }

    public void updateUsernameStudent(Long id, String username) {
        Student student = getStudentById(id);

        if (username == null || username.isEmpty()) throw new IllegalArgumentException("Username é obrigatório.");

        Optional<Student> existingStudentWithEmail = studentRepository.findByUsername(username);
        if (existingStudentWithEmail.isPresent() && !existingStudentWithEmail.get().getId().equals(id))
            throw new IllegalArgumentException("Este username já está sendo usado por outro estudante.");

        keycloakService.updateUsername(student.getKeycloakId(), username);

        student.setUsername(username);
        studentRepository.save(student);
    }

    public Student updateStudent (Long id, UpdateStudentDTO studentDTO) {
        Student existingStudent = getStudentById(id);

        if (existingStudent == null) throw new IllegalArgumentException("Estudante não existe.");

        studentMapper.updateStudentFromDTO(studentDTO, existingStudent);

        return studentRepository.save(existingStudent);
    }


    public Student getStudentLogged(String token){
        String keycloakId = keycloakService.getKeycloakIdByToken(token);
        Optional<Student> student = studentRepository.findByKeycloakId(keycloakId);

        if(student.isPresent()){
            return student.get();
        }else{
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
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
        if (studentRepository.findByEmail(studentRecord.getEmail()).isPresent()) throw new IllegalArgumentException("Estudante com este e-mail já existe.");

        if (studentRepository.findByUsername(studentRecord.getUsername()).isPresent()) throw new IllegalArgumentException("Estudante com este username já existe.");

        if (!studentRecord.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("E-mail inválido.");

        validatePassword(studentRecord.getPassword());

        UseKeycloakRegistrationDTO userKeycloakRecord = userKeycloakMapper.toKeycloakDTO(studentRecord);
        String keycloakUserId = keycloakService.createUser(userKeycloakRecord);

        if (keycloakUserId == null) throw new IllegalStateException("Erro ao criar usuário no Keycloak.");

        Student student = studentMapper.toStudent(studentRecord);
        student.setKeycloakId(keycloakUserId);
        return studentRepository.save(student);
    }

}
