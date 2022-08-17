package ru.azor.auth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public int sendMail(String to) {
        int code = new Random().nextInt(9000) + 1000;
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("gb-shop-winter");
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject("Подтверждение регистрации");
        simpleMailMessage.setText("Код для подтверждения регистрации: " + code);
        javaMailSender.send(simpleMailMessage);
        return code;
    }
}