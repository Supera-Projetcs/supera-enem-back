package com.supera.enem.seed;

import com.supera.enem.service.QuestionSeederService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class QuestionSeeder implements CommandLineRunner {

    @Autowired
    private QuestionSeederService questionSeederService;

    @Override
    public void run(String... args) throws Exception {
        questionSeederService.seedQuestions();
    }
}
