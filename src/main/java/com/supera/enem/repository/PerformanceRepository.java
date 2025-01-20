package com.supera.enem.repository;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

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


    @Query("""
        SELECT p
        FROM Performance p
        WHERE p.student.id = :studentId
          AND p.createdAt = (
              SELECT MAX(p2.createdAt)
              FROM Performance p2
              WHERE p2.student.id = :studentId
                AND p2.content.id = p.content.id
          )
    """)
    List<Performance> findLatestPerformancesByStudent(@Param("studentId") Long studentId);

    List<Performance> findAllByStudent_Id(Long studentId);
    Optional<Performance> findByStudentAndContent(Student student, Content content);
}
