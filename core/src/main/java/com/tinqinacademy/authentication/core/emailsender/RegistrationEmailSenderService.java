package com.tinqinacademy.authentication.core.emailsender;

public interface RegistrationEmailSenderService {
    void sendEmail(String to, String subject, String content);
}
