package com.techweb.paymentservice.service;

import com.techweb.paymentservice.entity.OutboxEvent;
import com.techweb.paymentservice.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void processPaymentResponse(String orderId, boolean success) {
        String status = success ? "SUCCESS" : "FAILED";

        // Create outbox event for payment response
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setEventType("PAYMENT_RESPONSE");
        outboxEvent.setPayload(orderId + ":" + status);
        outboxRepository.save(outboxEvent);
    }
}