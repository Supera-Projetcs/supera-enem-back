package com.supera.enem.repository;

import com.supera.enem.domain.Student;
import com.supera.enem.domain.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
//updated repository
@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    @Query("SELECT t FROM TestEntity t WHERE t.date <= CURRENT_DATE")
    Page<TestEntity> findCompletedTests(Pageable pageable);

    @Query("SELECT t FROM TestEntity t WHERE t.student = :student AND t.date BETWEEN :startDate AND :endDate ORDER BY t.date DESC")
    List<TestEntity> findByStudentAndDateBetween(Student student, java.sql.Date startDate, java.sql.Date endDate);

    long countByStudent(Student student);
}
