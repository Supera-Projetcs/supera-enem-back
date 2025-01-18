package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.domain.Question;
import com.supera.enem.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/* import java.util.Optional; */

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PatchMapping
    public Question saveQuestion(@RequestBody Question question) {
        return questionService.save(question);
    }

    @GetMapping
    public List<QuestionResponseDTO> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/{id}")
    public QuestionResponseDTO getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

}
