package com.supera.enem.schedule;

import com.supera.enem.domain.Student;
import com.supera.enem.repository.StudentRepository;
import com.supera.enem.service.EmailConsumer;
import com.supera.enem.service.EmailProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmailScheduler {

    @Autowired
    private EmailConsumer emailConsumer;
    @Autowired
    private StudentRepository studentRepository;

    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyNotifications() {
        List<Student> students = studentRepository.findAll();

        List<String> studentEmails = students.stream()
                .map(Student::getEmail)
                .collect(Collectors.toList());

        String subject = "Notificação Diária";
        String text = "Olá, confira suas atualizações diárias!";

        emailConsumer.sendEmail(studentEmails, subject, text);
        System.out.println("Notificações enviadas para " + studentEmails.size() + " estudantes.");
    }
}
