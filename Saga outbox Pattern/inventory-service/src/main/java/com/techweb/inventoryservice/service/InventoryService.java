package com.techweb.inventoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techweb.inventoryservice.entity.Order;
import com.techweb.inventoryservice.entity.OutboxEvent;
import com.techweb.inventoryservice.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public InventoryService(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment-processed", groupId = "order-group")
    public void checkInventory(String message) throws JsonProcessingException {
        String[] parts = message.split(":");  // Example message: "orderId:12345:SUCCESS"
        Long orderId = Long.parseLong(parts[0]);
        String paymentStatus = parts[1];

        if ("SUCCESS".equals(paymentStatus)) {
            boolean stockAvailable = checkStockAvailability(orderId);

            String status = stockAvailable ? "SUCCESS" : "FAILED";

            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setEventType("inventory-check-processed");
            outboxEvent.setPayload(orderId + ":" + status);
            outboxEvent.setProcessed(false);

            outboxRepository.save(outboxEvent);
        }
    }

    private boolean checkStockAvailability(Long orderId) {
        return true;
    }
}