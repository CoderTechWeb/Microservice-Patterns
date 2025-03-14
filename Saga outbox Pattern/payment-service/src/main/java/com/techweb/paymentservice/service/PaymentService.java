package com.techweb.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techweb.paymentservice.entity.Order;
import com.techweb.paymentservice.entity.OutboxEvent;
import com.techweb.paymentservice.repository.OutboxRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentService(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-created", groupId = "order-group")
    public void processPayment(String message) throws JsonProcessingException {
        Order order = objectMapper.readValue(message, Order.class);

        Long orderId = order.getId(); // Assuming the order object has an 'id' field
         boolean paymentSuccess = processPayment(orderId);

        String status = paymentSuccess ? "SUCCESS" : "FAILED";

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("payment-processed");
        outboxEvent.setPayload(orderId + ":" + status);
        outboxEvent.setProcessed(false);

        outboxRepository.save(outboxEvent);
    }

    private boolean processPayment(Long orderId) {
        return true;
    }
}