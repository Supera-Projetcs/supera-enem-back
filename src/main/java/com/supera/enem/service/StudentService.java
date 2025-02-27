package com.supera.enem.service;

import com.supera.enem.controller.DTOS.Student.*;
import com.supera.enem.controller.DTOS.UseKeycloakRegistrationDTO;
import com.supera.enem.controller.DTOS.StudentSubject.*;

import com.supera.enem.domain.*;

import com.supera.enem.exception.*;
import com.supera.enem.mapper.*;

import com.supera.enem.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;


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
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentSubjectRepository studentSubjectRepository;

    @Autowired
    private StudentSubjectMapper studentSubjectMapper;


    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudante com o ID " + id + " não foi encontrado."));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }


    public Student updateEmailStudent(Long id, String newEmail) {
        if (newEmail == null || newEmail.isEmpty()) throw new IllegalArgumentException("E-mail não pode ser nulo.");

        Student student = getStudentById(id);

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
        if (password.length() <= 3) {
            throw new BusinessException("A senha deve ter pelo menos 3 caracteres.");
        }
        if (!password.matches(".*\\d.*")) { // Verifica se contém pelo menos um número
            throw new BusinessException("A senha deve conter pelo menos 1 número.");
        }
        if (!password.matches(".*[A-Z].*")) { // Verifica se contém pelo menos uma letra maiúscula
            throw new BusinessException("A senha deve conter pelo menos uma letra maiúscula.");
        }
        if (!password.matches(".*[@#!$%^&*].*")) { // Verifica se contém pelo menos um símbolo
            throw new BusinessException("A senha deve conter pelo menos um símbolo.");
        }
    }

    public Student createStudent(StudentRequestDTO studentRecord) {

        if (studentRepository.findByEmail(studentRecord.getEmail()).isPresent()) throw new ResourceAlreadyExists("Estudante com este e-mail já existe.");

        if (studentRepository.findByUsername(studentRecord.getUsername()).isPresent()) throw new ResourceAlreadyExists("Estudante com este username já existe.");

        if (!studentRecord.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new BusinessException("E-mail inválido.");

        validatePassword(studentRecord.getPassword());

        UseKeycloakRegistrationDTO userKeycloakRecord = userKeycloakMapper.toKeycloakDTO(studentRecord);
        String keycloakUserId = keycloakService.createUser(userKeycloakRecord);

        if (keycloakUserId == null) throw new BusinessException("Erro ao criar usuário no Keycloak.");

        Student student = studentMapper.toStudent(studentRecord);
        student.setKeycloakId(keycloakUserId);

        return studentRepository.save(student);
    }

    public List<StudentSubjectResponseDTO> createStudentSubjects(Long studentId, List<StudentSubjectRequestDTO> listDto) {
        if(listDto.size() < subjectRepository.findAll().size()) throw new BusinessException("Falta matéria");
        Student student = getStudentById(studentId);

        List<StudentSubject> listStudentSubjects = listDto.stream()
                .map(dto -> {
            Subject subject = subjectRepository.findById(dto.getSubjectId()).orElse(null);
            StudentSubject studentSubject = new StudentSubject();
            studentSubject.setStudent(student);
            studentSubject.setSubject(subject);
            studentSubject.setSubjectWeight(dto.getSubjectWeight());
            return studentSubject;

        }).toList();

        studentSubjectRepository.saveAll(listStudentSubjects);

        return listStudentSubjects.stream().map(studentSubjectMapper::toDto).collect(Collectors.toList());
    }



}
