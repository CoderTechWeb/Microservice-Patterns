package com.techweb.orderservice.controller;

import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService sagaOrchestrator;

    @Autowired
    private RestTemplate restTemplate;

   // @CircuitBreaker(name = "orderServiceCB", fallbackMethod = "fallbackPaymentService")
    @PostMapping("/create")
    public String createOrder(@RequestBody Order order) {
        return restTemplate.getForObject("http://localhost:9000/payment/process", String.class);
    }

    public String fallbackPaymentService(Exception e) {
        return "Payment Service is currently unavailable. Please try again later.";
    }
}