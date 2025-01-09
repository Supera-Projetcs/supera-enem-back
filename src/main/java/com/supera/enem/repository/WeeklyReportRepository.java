package com.supera.enem.repository;

import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {

    List<WeeklyReport> findByStudent(Student student);

}
