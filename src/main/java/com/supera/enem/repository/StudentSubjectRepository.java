package com.supera.enem.repository;

import com.supera.enem.domain.StudentSubject;
import com.supera.enem.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {
    StudentSubject findStudentSubjectBySubject_Id(Long id);
}
