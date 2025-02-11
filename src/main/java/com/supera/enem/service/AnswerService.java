package com.supera.enem.service;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.controller.DTOS.AnswerResponseDTO;
import com.supera.enem.domain.*;
import com.supera.enem.exception.ResourceAlreadyExists;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.AnswerMapper;
import com.supera.enem.repository.AnswerRepository;
import com.supera.enem.repository.PerformanceRepository;
import com.supera.enem.repository.QuestionRepository;
import com.supera.enem.repository.TestRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class AnswerService {
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerMapper answerMapper;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private PerformanceRepository performanceRepository;

    @Transactional
    public AnswerResponseDTO createAnswer(AnswerRequestDTO answerRequestDTO) {
        Answer answer = answerMapper.toEntity(answerRequestDTO);
        Question question = questionRepository.findById(answerRequestDTO.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getClaim("sub");

        TestEntity test = testRepository.findById(answerRequestDTO.getTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        boolean answerExists = answerRepository.existsByTestEntityAndQuestion(test, question);
        if (answerExists) {
            throw new ResourceAlreadyExists("An answer already exists for this question in the test");
        }


        if (!test.getStudent().getKeycloakId().equals(keycloakId)) {
            throw new ResourceNotFoundException("The authenticated student is not related to this test");
        }

        answer.setCorrect(question.getAnswer() == answer.getText());
        answer.setQuestion(question);
        answer.setTestEntity(test);
        answerRepository.save(answer);
        System.out.println("Answer: " + answer);
        System.out.println("Answer question: " + answer.getQuestion());
        System.out.println("Answer testEntity: " + answer.getTestEntity());
        updatePerformance(answer);
        return answerMapper.toDTO(answer);
    }


    public void updatePerformance(Answer answer){
        if (answer.getTestEntity() == null || answer.getQuestion() == null) {
            throw new IllegalArgumentException("Answer must be associated with a Test and a Question");
        }

        Student student = answer.getTestEntity().getStudent();

        Set<Content> contents = answer.getQuestion().getContents();

        if (contents.isEmpty()) {
            throw new IllegalArgumentException("Question must be associated with at least one Content");
        }

        for (Content content : contents) {
            Performance performance = performanceRepository
                    .findByStudentAndContent(student, content)
                    .orElseGet(() -> {
                        Performance newPerformance = new Performance();
                        newPerformance.setStudent(student);
                        newPerformance.setContent(content);
                        newPerformance.setPerformanceRate(0.0);
                        newPerformance.setCreatedAt(LocalDateTime.now());
                        return newPerformance;
                    });

            List<Answer> answersForContent = answerRepository.findAnswersByStudentAndContent(student, content);

            long correctAnswers = answersForContent.stream().filter(Answer::isCorrect).count();
            long totalAnswers = answersForContent.size();
            double performanceRate = (double) correctAnswers / totalAnswers * 2 - 1; // Normaliza para -1 a 1

            performance.setPerformanceRate(performanceRate);
            performanceRepository.save(performance);
        }

    }

    public List<AnswerResponseDTO> getAnswersByTest(Long testId) {
        TestEntity test = testRepository.findById(testId)
                .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String keycloakId = jwt.getClaim("sub");

        if (!test.getStudent().getKeycloakId().equals(keycloakId)) {
            throw new ResourceNotFoundException("The authenticated student is not related to this test");
        }

        List<Answer> answers = answerRepository.findAll();
        return answerMapper.toDTOList(answers);
    }


}
