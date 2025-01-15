package com.supera.enem.repository;

import com.supera.enem.domain.Student;
import com.supera.enem.domain.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    @Query("SELECT t FROM Test t WHERE t.date <= CURRENT_DATE")
    List<TestEntity> findCompletedTests();

    List<TestEntity> findByStudentAndDateBetween(Student student, java.sql.Date startDate, java.sql.Date endDate);
}
