package com.techweb.orderservice.controller;

import com.techweb.orderservice.data.OrderEvent;
import com.techweb.orderservice.dto.OrderRequest;
import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest request) {
        Order order = new Order(null, request.getProductId(), "PENDING");
        orderRepository.save(order);
        kafkaTemplate.send("order-topic", new OrderEvent(order.getId(), "CREATED"));
        return ResponseEntity.ok("Order placed!");
    }

}
