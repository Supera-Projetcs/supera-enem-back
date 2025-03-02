package com.supera.enem.service;

import com.supera.enem.controller.DTOS.Performace.PerformaceResponseDTO;
import com.supera.enem.controller.DTOS.SubjectDifficultyDTO;
import com.supera.enem.controller.DTOS.Performace.InitialPerformaceRequestDTO;
import com.supera.enem.domain.Content;
import com.supera.enem.domain.Performance;
import com.supera.enem.domain.Student;
import com.supera.enem.domain.Subject;
import com.supera.enem.exception.BusinessException;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.PerformanceMapper;
import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.PerformanceRepository;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private PerformanceMapper performanceMapper;

    @Autowired
    private StudentRepository studentRepository;

    public List<SubjectDifficultyDTO> getSubjectDifficulties(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID must not be null");
        }

        List<Object[]> results = performanceRepository.findAveragePerformanceBySubject(studentId);
        return results.stream().map(result -> {
            String subjectName = (String) result[0];
            double avgPerformance = ((Number) result[1]).doubleValue();

            // Convertendo de -1.0 a 1.0 para 0 a 100
            double percentage = ((avgPerformance + 1) / 2) * 100;

            if (percentage < 0 || percentage > 100) {
                throw new IllegalArgumentException("Invalid performance value after conversion: " + percentage);
            }

            return new SubjectDifficultyDTO(subjectName, percentage);
        }).collect(Collectors.toList());
    }



    public List<PerformaceResponseDTO> createInitialPerformance(Long studentId, List<InitialPerformaceRequestDTO> listDto) {
        if(listDto.size() < subjectRepository.findAll().size()) throw new BusinessException("Não tem perfomace inicial para todos as matérias.");
        System.out.println(listDto);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudante não encontrado"));

        List<Content> contents = contentRepository.findAll();
        List<Performance> performances = new ArrayList<Performance>();

        contents.forEach(content -> {

            Stream<InitialPerformaceRequestDTO> filteredList = listDto.stream().filter(item -> item.getSubjectId().equals(content.getSubject().getId()));
            Stream<Double> mapList =  filteredList.map(InitialPerformaceRequestDTO::getPerformaceValue);

            Double performanceRate = mapList
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("Performance não encontrada para o subjectId: " + content.getSubject().getId()));

            Performance newPerformance = new Performance();
            newPerformance.setStudent(student); // Associa o estudante
            newPerformance.setContent(content); // Associa o conteúdo
            newPerformance.setCreatedAt(LocalDateTime.now()); // Define o horário atual
            newPerformance.setPerformanceRate(performanceRate); // Define o desempenho encontrado

            performances.add(newPerformance);
        });

        performanceRepository.saveAll(performances);

        return performanceMapper.toDTO_LIST(performances);
    }


}
