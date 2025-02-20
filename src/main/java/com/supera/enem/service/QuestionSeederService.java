package com.supera.enem.service;

import com.supera.enem.domain.Question;
import com.supera.enem.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionSeederService {

    @Autowired
    private QuestionRepository questionRepository;

    public void seedQuestions() {
        System.out.println("Chamando seedQuestions()..."); // Apenas para depuração
        List<Question> questions = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            Question question = new Question();
            question.setText("Question " + i);
            question.setAnswer((char) ('A' + (i % 4)));

            Map<String, String> answers = new HashMap<>();
            answers.put("A", "Answer A for question " + i);
            answers.put("B", "Answer B for question " + i);
            answers.put("C", "Answer C for question " + i);
            answers.put("D", "Answer D for question " + i);
            question.setAnswers(answers);

            questions.add(question);
        }

        questionRepository.saveAll(questions);
    }

}
