package com.supera.enem.controller;

import com.supera.enem.controller.DTOS.QuestionResponseDTO;
import com.supera.enem.domain.Image;
import com.supera.enem.domain.Question;
import com.supera.enem.repository.ImageRepository;
import com.supera.enem.repository.QuestionRepository;
import com.supera.enem.service.MinioService;
import com.supera.enem.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
/* import java.util.Optional; */

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private MinioService minioService;
    @Autowired
    private ImageRepository imageRepository;

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

    @PostMapping("/{questionId}/upload-image")
    public ResponseEntity<?> uploadImage(@PathVariable Long questionId,
                                         @RequestParam("file") MultipartFile file) {
        try {
            Optional<Question> questionOptional = questionRepository.findById(questionId);
            if (questionOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Question question = questionOptional.get();

            String objectName = "images/" + file.getOriginalFilename();
            String fileUrl = minioService.uploadFile(objectName, file.getInputStream(), file.getContentType());

            // Cria o objeto Image e associa à questão
            Image image = new Image();
            image.setUrl(fileUrl);
            image.setDescription("Uploaded image");
            image.setQuestion(question);

            // Salva a imagem
            imageRepository.save(image);

            return ResponseEntity.ok(image);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading file: " + e.getMessage());
        }
    }
}
