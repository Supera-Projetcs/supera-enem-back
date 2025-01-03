package com.supera.enem.service;

import com.supera.enem.domains.Student;
import com.supera.enem.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }


    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student createStudent(Student student) {
//        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
//            throw new IllegalArgumentException("Estudante com este e-mail já existe.");
//        }
        return studentRepository.save(student);
    }

}
