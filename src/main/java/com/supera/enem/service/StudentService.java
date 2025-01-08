package com.supera.enem.service;

import com.supera.enem.controller.DTOS.StudentDTO;
import com.supera.enem.controller.DTOS.StudentRegistrationRecord;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.Address;

import com.supera.enem.repository.StudentRepository;
import com.supera.enem.service.keycloak.KeycloackUserServiceImplematation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StudentService {

    private final StudentRepository studentRepository;
    @Autowired
    private KeycloackUserServiceImplematation keycloakImplematation;
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudante com o ID " + id + " não foi encontrado."));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student createStudent(StudentDTO studentRecord) {

        if (studentRepository.findByEmail(studentRecord.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Estudante com este e-mail já existe.");
        }

        // Criar usuário no keycloak e pegar o id
        StudentRegistrationRecord userKeycloakRecord = new StudentRegistrationRecord(
                studentRecord.getUsername(),
                studentRecord.getEmail(),
                studentRecord.getFirstName(),
                studentRecord.getLastName(),
                studentRecord.getPassword()
        );

        String keycloakUserId = keycloakImplematation.createUser(userKeycloakRecord);
        if (keycloakUserId == null) {
            throw new IllegalStateException("Erro ao criar usuário no Keycloak.");
        }

        //Criar endereço
        Address address = new Address();
        address.setStreet(studentRecord.getAddress().getStreet());
        address.setCity(studentRecord.getAddress().getCity());
        address.setState(studentRecord.getAddress().getState());
        address.setZipCode(studentRecord.getAddress().getZipCode());
        address.setHouseNumber(studentRecord.getAddress().getHouseNumber());

        //Criar estudante
        Student student = new Student();
        student.setKeycloakId(keycloakUserId);
        student.setFirstName(studentRecord.getFirstName());
        student.setLastName(studentRecord.getLastName());
        student.setDreamCourse(studentRecord.getDreamCourse());
        student.setPhone(studentRecord.getPhone());
        student.setEmail(studentRecord.getEmail());
        student.setBirthDate(studentRecord.getBirthDate());
        student.setAddress(address);
        student.setPreferredStudyDays(studentRecord.getPreferredStudyDays());
        student.setRegistered(true);

        return studentRepository.save(student);
    }

}
