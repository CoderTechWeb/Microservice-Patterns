package com.techweb.orderservice.service;

import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.entity.OutboxEvent;
import com.techweb.orderservice.repository.OrderRepository;
import com.techweb.orderservice.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxRepository outboxEventRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public Order createOrder(String product, int quantity) {
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setStatus("CREATED");
        order = orderRepository.save(order);

        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateId(order.getId().toString());
        outboxEvent.setEventType("ORDER_CREATED");
        outboxEvent.setPayload("{\"orderId\":" + order.getId() + ", \"product\":\"" + order.getProduct() + "\"}");
        outboxEvent.setProcessed(false);
        outboxEventRepository.save(outboxEvent);

        kafkaTemplate.send("order-topic", order.getId().toString());
        return order;
    }

    @KafkaListener(topics = "order-inventory-checked", groupId = "order-group")
    public void updateOrderStatus(String message) {
        // Parse the message to extract orderId and status
        String[] parts = message.split(":");
        Long orderId = Long.parseLong(parts[0].replaceAll("[^\\d.]", ""));
        String status = parts[1].replaceAll("[^a-zA-Z]", "");

        // Find the order and update its status
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        if ("SUCCESS".equals(status)) {
            order.setStatus("COMPLETED");
        } else {
            order.setStatus("CANCELLED");
        }
        orderRepository.save(order);
    }
}