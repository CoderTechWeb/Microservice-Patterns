package com.techweb.paymentservice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessor {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "order-topic", groupId = "payment-group")
    public void processOrder(String orderId) {
        boolean paymentSuccess = processPayment();
        if (paymentSuccess) {
            kafkaTemplate.send("inventory-check", orderId);
        } else {
            kafkaTemplate.send("payment-response", orderId + ":FAILED");
        }
    }

    private boolean processPayment() {
        return true; // Simulate a successful payment
    }
}