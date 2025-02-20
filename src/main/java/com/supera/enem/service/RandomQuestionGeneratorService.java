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

        if (contents.isEmpty()) {
            System.out.println("Nenhum conteúdo encontrado. Nada para processar.");
            return;
        }

        if (allQuestions.isEmpty()) {
            System.out.println("Nenhuma pergunta encontrada. Nada para atribuir.");
            return;
        }

        if (allQuestions.size() < 3) {
            throw new IllegalArgumentException("Insufficient questions to assign");
        }

        for (Content content : contents) {

            int questionsToAssign = Math.min(allQuestions.size(), 5);

            List<Question> randomQuestions = getRandomQuestions(allQuestions, questionsToAssign);

            for (Question question : randomQuestions) {
                content.getQuestions().add(question);
                question.getContents().add(content);
            }

            contentRepository.save(content);

            System.out.println("Content ID: " + content.getId() + " - Questions: " + content.getQuestions());
        }
    }

    private List<Question> getRandomQuestions(List<Question> questions, int count) {
        if (questions.size() < count) {
            throw new IllegalArgumentException("Requested more questions than available");
        }

        Random random = new Random();
        return random.ints(0, questions.size())
                .distinct()
                .limit(count)
                .mapToObj(questions::get)
                .toList();
    }
}
