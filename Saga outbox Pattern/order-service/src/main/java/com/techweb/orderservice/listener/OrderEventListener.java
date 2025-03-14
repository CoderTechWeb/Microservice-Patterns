package com.techweb.orderservice.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techweb.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderEventListener {

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "inventory-check-processed", groupId = "order-group")
    public void listenOrderStatusUpdate(String message) {
        try {
            String[] parts = message.split(":");  // Example message: "orderId:12345:SUCCESS"
            Long orderId = Long.parseLong(parts[0]);
            String status = parts[1];

            orderService.updateOrderStatus(orderId, status);

        } catch (Exception e) {
            //log.error("Error processing order status update event", e);
        }
    }
}