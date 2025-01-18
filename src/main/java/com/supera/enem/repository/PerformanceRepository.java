package com.supera.enem.repository;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.supera.enem.domain.Performance;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {

    @Query("SELECT s.name AS subjectName, AVG(p.performanceRate) AS avgPerformance " +
            "FROM Performance p " +
            "JOIN Content c ON p.content.id = c.id " +
            "JOIN Subject s ON c.subject.id = s.id " +
            "WHERE p.student.id = :studentId " +
            "GROUP BY s.name")
    List<Object[]> findAveragePerformanceBySubject(Long studentId);

    Optional<Performance> findByStudentAndContent(Student student, Content content);
}
