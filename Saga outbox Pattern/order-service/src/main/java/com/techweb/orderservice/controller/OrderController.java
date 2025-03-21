package com.techweb.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) throws JsonProcessingException {
        Order serviceOrder = orderService.createOrder(order);
        return ResponseEntity.ok(serviceOrder);
    }
}