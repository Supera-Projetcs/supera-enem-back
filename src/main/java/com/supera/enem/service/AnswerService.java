package com.supera.enem.service;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.controller.DTOS.AnswerResponseDTO;
import com.supera.enem.domain.Answer;
import com.supera.enem.domain.Question;
import com.supera.enem.domain.TestEntity;
import com.supera.enem.exception.ResourceAlreadyExists;
import com.supera.enem.exception.ResourceNotFoundException;
import com.supera.enem.mapper.AnswerMapper;
import com.supera.enem.repository.AnswerRepository;
import com.supera.enem.repository.QuestionRepository;
import com.supera.enem.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return answerMapper.toDTO(answer);
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
