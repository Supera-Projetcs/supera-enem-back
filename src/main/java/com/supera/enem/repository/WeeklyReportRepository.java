package com.supera.enem.repository;

import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {

    List<WeeklyReport> findByStudent(Student student);

    Optional<WeeklyReport> findByIdAndStudent(Long id, Student student);

    @Query("SELECT wr FROM WeeklyReport wr WHERE wr.student.id = :studentId ORDER BY wr.date DESC")
    Optional<WeeklyReport> findTopByStudentOrderByDateDesc(@Param("studentId") Long studentId);

    List<WeeklyReport> findByStudentIdAndDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);

}
