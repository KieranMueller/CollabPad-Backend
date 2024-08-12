package com.kieran.notepad.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {
        log.info("Received request to send email with details, to: {}, subject: {}, body: {}", to, subject, body);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("mailtrap@kieranmueller.com");
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }
}
