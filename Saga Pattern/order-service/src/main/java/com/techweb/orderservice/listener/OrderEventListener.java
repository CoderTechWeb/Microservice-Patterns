package com.techweb.orderservice.listener;

import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "payment-response", groupId = "order-group")
    public void processPaymentResponse(String message){
        String[] data = message.split(":");
        Long orderId = Long.parseLong(data[0]);
        String status = data[1];

        Order order = orderRepository.findById(orderId).orElseThrow();
        if ("FAILED".equals(status)) {
            order.setStatus("CANCELLED");
        } else {
            order.setStatus("COMPLETED");
        }
        orderRepository.save(order);
    }
}