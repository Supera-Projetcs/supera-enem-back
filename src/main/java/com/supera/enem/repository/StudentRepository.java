package com.supera.enem.repository;

import com.supera.enem.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findByUsername(String username);
    Student findByKeycloakId(String keycloakId);
    Boolean existsByEmail(String email);


}
