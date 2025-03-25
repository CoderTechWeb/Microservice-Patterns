package com.techweb.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/test")
    public ResponseEntity<String> testOrderService() {
        return ResponseEntity.ok("Order Service is working!");
    }
}