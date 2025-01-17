package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.AnswerRequestDTO;
import com.supera.enem.controller.DTOS.AnswerResponseDTO;
import com.supera.enem.service.AnswerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
@Tag(name = "Answer", description = "Answer API")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    @PostMapping
    public AnswerResponseDTO createAnswer(@RequestBody AnswerRequestDTO answerRequestDTO) {
        return answerService.createAnswer(answerRequestDTO);
    }

    @GetMapping("/{testId}")
    public List<AnswerResponseDTO> getAnswersByTest(@PathVariable Long testId) {
        return answerService.getAnswersByTest(testId);
    }
}
