package com.tinqinacademy.authentication.core.emailsender;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String content);
}
