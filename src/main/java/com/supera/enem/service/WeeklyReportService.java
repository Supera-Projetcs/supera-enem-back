package com.supera.enem.service;

import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.mapper.WeeklyReportMapper;
import com.supera.enem.repository.WeeklyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeeklyReportService {

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;

    private final WeeklyReportMapper weeklyReportMapper = WeeklyReportMapper.INSTANCE;

    public List<WeeklyReport> getWeeklyReportsByStudent(Student student) {
        return weeklyReportRepository.findByStudent(student);
    }
}
