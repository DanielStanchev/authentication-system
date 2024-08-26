package com.tinqinacademy.authentication.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.authentication.kafka.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaEmailProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendEmailMessage(EmailMessage message) {
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            kafkaTemplate.send("email", messageJson);
        } catch (Exception e) {
            log.error(String.format("Error sending message: %s", e.getMessage()));
        }
    }
}
