package com.supera.enem.service;

import com.supera.enem.controller.DTOS.AlitaRequestDTO;
import com.supera.enem.controller.DTOS.WeeklyReportDTO;
import com.supera.enem.controller.DTOS.WeeklyReportRequestDTO;

import com.supera.enem.domain.*;

import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.PerformanceMapper;
import com.supera.enem.mapper.WeeklyReportMapper;
import com.supera.enem.repository.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

@Service
public class WeeklyReportService {

    @Autowired
    private WeeklyReportRepository weeklyReportRepository;

    @Autowired
    private AuthenticatedService authenticatedService;

    @Autowired
    private  WeeklyReportMapper weeklyReportMapper ;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private PerformanceMapper performanceMapper;
    @Autowired
    private StudentSubjectRepository studentSubjectRepository;
    @Autowired
    private SubjectRepository subjectRepository;


    public List<WeeklyReportDTO> getWeeklyReportsByStudent(Student student) {
        return weeklyReportRepository.findByStudent(student).stream()
                .map(weeklyReportMapper::toDto)
                .collect(Collectors.toList());
    }

    public WeeklyReportDTO getWeeklyReportById(Long id, Student student) {
      WeeklyReport weeklyReport =  weeklyReportRepository.findByIdAndStudent(id, student).orElseThrow(()-> new ResourceNotFoundException("WeeklyReport not exist"));

      return weeklyReportMapper.toDto(weeklyReport);
    }

    private List<AlitaRequestDTO> getAlitaReportsByStudent(Long studentId) {
        String url = "http://alita.yasc.com.br/contents/";
        List<Performance> performances = performanceRepository.findLatestPerformancesByStudent(studentId);


        List<AlitaRequestDTO> alitaList =  performances.stream().map(performance -> {
            Double pesoSubclasse = studentSubjectRepository.findStudentSubjectBySubject_Id(performance.getContent().getSubject().getId()).getSubjectWeight();

            AlitaRequestDTO alitaRequestDTO = new AlitaRequestDTO();
            alitaRequestDTO.setId(performance.getContent().getId());
            alitaRequestDTO.setDesempenho(performance.getPerformanceRate());
            alitaRequestDTO.setPeso_da_classe(performance.getContent().getContent_weight());
            alitaRequestDTO.setPeso_por_questao(performance.getContent().getQuestion_weight());
            alitaRequestDTO.setClasse(performance.getContent().getName());
            alitaRequestDTO.setSubclasse(performance.getContent().getSubject().getName());
            alitaRequestDTO.setPeso_da_subclasse(pesoSubclasse);
            return alitaRequestDTO;
        }).toList();

        try {

            List<AlitaRequestDTO> response =  restTemplate.postForObject(url, alitaList, List.class);
            System.out.println("Resposta do servidor: " + response);

            return response;
        } catch (Exception ex) {
            throw new RuntimeException("Erro ocorreu", ex);
        }

    }

    private WeeklyReport generateWeeklyReport(List<?> rawList, Student student) {

        WeeklyReport weeklyReport = new WeeklyReport();
        weeklyReport.setStudent(student);
        weeklyReport.setDate(new Date());
        Set<Content> contents = rawList.stream()
                .map(obj -> {
                    Long id = null;

                    if (obj instanceof LinkedHashMap<?, ?> map) {
                        Object idObj = map.get("ID");
                        if (idObj instanceof Number) {
                            id = ((Number) idObj).longValue();
                        }
                    }

                    if (id == null)
                        return null;

                    Content content = contentRepository.findById(id).orElse(null);

                    if (content == null)
                        System.out.println("Content not found with ID: " + id);


                    return content;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        System.out.println(contents);
         weeklyReport.setContents(contents);

        return weeklyReport;
    }

    public WeeklyReportDTO getWeeklyReport() {
        Student student = authenticatedService.getAuthenticatedStudent();
        LocalDate currentDate = LocalDate.now();
        LocalDate weekStart = currentDate.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.SUNDAY));
        LocalDate weekEnd = currentDate.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SATURDAY));

        WeeklyReport existingReport = weeklyReportRepository
                .findByStudentIdAndDateBetween(student.getId(), weekStart, weekEnd);

        if (existingReport != null) {
            return weeklyReportMapper.toDTO(existingReport);
        }

        List<AlitaRequestDTO> newWeeklyReport = getAlitaReportsByStudent(student.getId());
        WeeklyReport weeklyReport = generateWeeklyReport(newWeeklyReport, student);
        System.out.println(weeklyReport);
        return  weeklyReportMapper.toDTO(weeklyReportRepository.save(weeklyReport));
    }




    public WeeklyReportDTO updateWeeklyReport(WeeklyReportRequestDTO weeklyReportRequestDTO, Long id) {
        authenticatedService.getAuthenticatedStudent();

        WeeklyReport existingReport = weeklyReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WeeklyReport not found with ID: " + id));

        existingReport.setDate(weeklyReportRequestDTO.getDate());

        if (weeklyReportRequestDTO.getContentIds() != null && !weeklyReportRequestDTO.getContentIds().isEmpty()) {
            Set<Content> updatedContents = new LinkedHashSet<>(contentRepository.findAllById(weeklyReportRequestDTO.getContentIds()));
            existingReport.setContents(updatedContents);
        } else {
            existingReport.getContents().clear();
        }

        WeeklyReport updatedReport = weeklyReportRepository.save(existingReport);

        return weeklyReportMapper.toDTO(updatedReport);
    }


}
