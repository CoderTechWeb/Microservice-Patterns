package com.techweb.orderservice.service;

import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public Order createOrder(String product, int quantity) {
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(quantity);
        order.setStatus("CREATED");
        order = orderRepository.save(order);

        kafkaTemplate.send("order-topic", order.getId().toString());
        return order;
    }
}