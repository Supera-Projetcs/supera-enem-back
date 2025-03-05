package com.supera.enem.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendEmailNotification(String emailMessage) {
        rabbitTemplate.convertAndSend("emailQueue", emailMessage);
        System.out.println("Mensagem enviada para a fila: " + emailMessage);
    }
}