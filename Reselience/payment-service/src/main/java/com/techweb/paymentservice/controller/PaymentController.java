package com.techweb.paymentservice.controller;

import com.techweb.paymentservice.dto.PaymentRequest;
import com.techweb.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(request));
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refundPayment(@RequestParam Long orderId) {
        return ResponseEntity.ok(paymentService.refundPayment(orderId));
    }
}