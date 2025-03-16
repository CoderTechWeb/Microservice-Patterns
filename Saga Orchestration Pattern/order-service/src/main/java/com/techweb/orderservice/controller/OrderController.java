package com.techweb.orderservice.controller;

import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService sagaOrchestrator;

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(sagaOrchestrator.processOrder(order));
    }
}