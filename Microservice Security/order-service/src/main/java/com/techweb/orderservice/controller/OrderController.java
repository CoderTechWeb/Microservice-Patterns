package com.techweb.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')") // ✅ Only ADMIN can access
    public ResponseEntity<String> getAllOrders() {
        return ResponseEntity.ok("All Orders (Admin Access)");
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('USER')") // ✅ Only USER can access
    public ResponseEntity<String> getUserOrders() {
        return ResponseEntity.ok("User Orders");
    }


    @GetMapping("/test")
    public ResponseEntity<String> testOrderService() {
        return ResponseEntity.ok("Order Service is working!");
    }
}