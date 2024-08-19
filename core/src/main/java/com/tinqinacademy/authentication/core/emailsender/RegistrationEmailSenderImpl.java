package com.tinqinacademy.authentication.core.emailsender;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Slf4j
@Service
public class RegistrationEmailSenderImpl implements RegistrationEmailSenderService{

    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender emailSender;

    public RegistrationEmailSenderImpl(JavaMailSender emailSender) {this.emailSender = emailSender;}


    @Override
    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        emailSender.send(message);

        log.info("Email sent to {} from {}", to, from);
    }
}
