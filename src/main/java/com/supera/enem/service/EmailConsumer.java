package com.supera.enem.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

@Service
public class EmailConsumer {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(List<String> toEmails, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmails.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
        System.out.println("E-mails enviados para: " + toEmails);
    }
}