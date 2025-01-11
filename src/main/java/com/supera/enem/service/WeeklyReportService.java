package com.supera.enem.service;

import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;
import com.supera.enem.mapper.WeeklyReportMapper;
import com.supera.enem.repository.WeeklyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Service
public class WeeklyReportService {

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;

    private final WeeklyReportMapper weeklyReportMapper = WeeklyReportMapper.INSTANCE;

    public List<WeeklyReport> getWeeklyReportsByStudent(Student student) {
        return weeklyReportRepository.findByStudent(student);
    }

    public WeeklyReport getWeeklyReportById(Long id, Student student) {
        return weeklyReportRepository.findByIdAndStudent(id, student);
    }

    @Scheduled(cron = "0 0 14 ? * SAT")
    public void executeWeeklyTask() {
        try {

            System.out.println("Top");

        } catch (Exception e) {

        }
    }

}
