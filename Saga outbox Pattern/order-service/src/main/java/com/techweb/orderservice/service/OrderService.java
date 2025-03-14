package com.techweb.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.entity.OutboxEvent;
import com.techweb.orderservice.repository.OrderRepository;
import com.techweb.orderservice.repository.OutboxRepository;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrderService {

    private OrderRepository orderRepository;

    private OutboxRepository outboxRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(Order order) {
        order.setStatus("PENDING");
        orderRepository.save(order);

        saveOutboxEvent("order-created", order);

        return order;
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(status);
        orderRepository.save(order);

        saveOutboxEvent("ORDER_UPDATED", order);
    }

    private void saveOutboxEvent(String eventType, Order order) {
        try {
            OutboxEvent event = new OutboxEvent();
            event.setEventType(eventType);
            event.setPayload(objectMapper.writeValueAsString(order));
            event.setProcessed(false);
            outboxRepository.save(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize order event", e);
        }
    }
}