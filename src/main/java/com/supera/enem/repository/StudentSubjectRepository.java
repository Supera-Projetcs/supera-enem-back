package com.supera.enem.repository;

import com.supera.enem.domain.StudentSubject;
import com.supera.enem.domain.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Long> {
    Optional<StudentSubject> findStudentSubjectBySubject_IdAndStudent_Id(Long subjectId, Long studentId);
}
