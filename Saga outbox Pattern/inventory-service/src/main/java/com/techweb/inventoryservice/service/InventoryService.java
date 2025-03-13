package com.techweb.inventoryservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "inventory-check", groupId = "inventory-group")
    public void checkStock(String orderId) {
        boolean stockAvailable = checkStockAvailability();
        if (stockAvailable) {
            kafkaTemplate.send("payment-response", orderId + ":SUCCESS");
        } else {
            kafkaTemplate.send("payment-response", orderId + ":FAILED");
        }
    }

    private boolean checkStockAvailability() {
        return true;
    }
}