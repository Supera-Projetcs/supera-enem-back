package com.supera.enem.service;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Question;
import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class RandomQuestionGeneratorService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Transactional
    public void assignRandomQuestionsToContents() {
        List<Content> contents = contentRepository.findAll();
        List<Question> allQuestions = questionRepository.findAll();

        for (Content content : contents) {
            List<Question> randomQuestions = getRandomQuestions(allQuestions, 5);

            for (Question question : randomQuestions) {
                content.getQuestions().add(question);
                question.getContents().add(content);
            }

            contentRepository.save(content);

            System.out.println("Content ID: " + content.getId() + " - Questions: " + content.getQuestions());
        }
    }
    private List<Question> getRandomQuestions(List<Question> questions, int count) {
        Random random = new Random();
        return random.ints(0, questions.size())
                .distinct()
                .limit(count)
                .mapToObj(questions::get)
                .toList();
    }

}
