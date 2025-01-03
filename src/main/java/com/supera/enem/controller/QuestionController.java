package com.supera.enem.controller;

import com.supera.enem.domain.Question;
import com.supera.enem.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PatchMapping
    public Question saveQuestion(@RequestBody Question question) {
        return questionService.save(question);
    }

    @GetMapping("/{id}")
    public Optional<Question> getQuestion(@PathVariable Long id) {
        return questionService.findById(id);
    }

}
