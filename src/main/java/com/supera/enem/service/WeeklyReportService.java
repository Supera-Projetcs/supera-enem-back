package com.supera.enem.service;

import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;
import com.supera.enem.domain.Performance;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.WeeklyReportMapper;
import com.supera.enem.repository.PerformanceRepository;

import com.supera.enem.domain.Student;
import com.supera.enem.domain.WeeklyReport;

import com.supera.enem.repository.WeeklyReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;


@Service
public class WeeklyReportService {

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;


    private final WeeklyReportMapper weeklyReportMapper = WeeklyReportMapper.INSTANCE;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private RestTemplate restTemplate;



    public List<WeeklyReportDTO> getWeeklyReportsByStudent(Student student) {
        return weeklyReportRepository.findByStudent(student).stream()
                .map(weeklyReportMapper::toDto)
                .collect(Collectors.toList());
    }

    public WeeklyReportDTO getWeeklyReportById(Long id, Student student) {
      WeeklyReport weeklyReport =  weeklyReportRepository.findByIdAndStudent(id, student).orElseThrow(()-> new ResourceNotFoundException("WeeklyReport not exist"));

      return weeklyReportMapper.toDto(weeklyReport);
    }

    public WeeklyReport getWeeklyReport(Long studentId) {

        LocalDate currentDate = LocalDate.now();
        LocalDate weekStart = currentDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        LocalDate weekEnd = currentDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY));

        WeeklyReport existingReport = weeklyReportRepository
                .findByStudentIdAndDateBetween(studentId, weekStart, weekEnd);

        if (existingReport != null) {
            return existingReport;
        }else{
            String url = "http://alita.yasc.com.br/contents";
            List<Performance> performances = performanceRepository.findLatestPerformancesByStudent(studentId);

            System.out.println("URL gerada: " + url);
            System.out.println("Conteúdo da requisição: " + performances);


            String response = restTemplate.postForObject(url, performances, String.class);


            System.out.println("Resposta do servidor: " + response);
        }




        return null;
    }



}
