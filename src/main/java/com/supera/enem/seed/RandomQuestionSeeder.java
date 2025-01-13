package com.supera.enem.seed;

import com.supera.enem.service.RandomQuestionGeneratorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class RandomQuestionSeeder implements CommandLineRunner {
    private final RandomQuestionGeneratorService randomQuestionGeneratorService;

    public RandomQuestionSeeder(RandomQuestionGeneratorService randomQuestionGeneratorService) {
        this.randomQuestionGeneratorService = randomQuestionGeneratorService;
    }

    @Override
    public void run(String... args) throws Exception {
        randomQuestionGeneratorService.assignRandomQuestionsToContents();
    }
}
