package com.supera.enem.seed;

import com.supera.enem.domain.Content;
import com.supera.enem.domain.Subject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.supera.enem.repository.ContentRepository;
import com.supera.enem.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
public class ContentDataSeeder implements CommandLineRunner {
    private final ContentRepository contentRepository;
    private final SubjectRepository subjectRepository;

    public ContentDataSeeder(ContentRepository contentRepository, SubjectRepository subjectRepository) {
        this.contentRepository = contentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadCsv();
    }

    public void loadCsv() throws IOException {
        Resource resource = new ClassPathResource("loaddata.csv");
        InputStream inputStream = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        reader.readLine();
        while ((line = reader.readLine()) != null) {
            String[] fields = line.split(",");
            String contentName = fields[2];
            String subjectName = fields[1];

            Subject subject = this.subjectRepository.findByName(subjectName);
            if (subject == null) {
                subject = new Subject();
                subject.setName(subjectName);
                this.subjectRepository.save(subject);
            }

            Content content = this.contentRepository.findByName(contentName);
            if (content == null) {
                content = new Content();
                content.setName(contentName);
                content.setQuestion_weight(Double.parseDouble(fields[6]));
                content.setContent_weight(Double.parseDouble(fields[5]));
                this.contentRepository.save(content);
            }

            content.setSubject(subject);
            this.contentRepository.save(content);
//            System.out.println("Content: " + contentName + " Subject: " + subjectName + " Question Weight: " + fields[6] + " Content Weight: " + fields[5]);
        }

    }
}
