package com.supera.enem.service;

import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.domain.Question;
import com.supera.enem.mapper.QuestionMapper;
import com.supera.enem.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    @Autowired
    private QuestionRepository questionRepository;
    private final QuestionMapper questionMapper = QuestionMapper.INSTANCE;

    public List<QuestionResponseDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(questionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public QuestionResponseDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return questionMapper.toDTO(question);
    }

    public Question save(Question question) {
        return questionRepository.save(question);
    }

    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }
}
