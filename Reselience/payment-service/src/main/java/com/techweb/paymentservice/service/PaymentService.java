package com.techweb.paymentservice.service;

import com.techweb.paymentservice.dto.PaymentRequest;
import com.techweb.paymentservice.entity.Payment;
import com.techweb.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public String processPayment(PaymentRequest request) {
        Payment payment = new Payment();
        if (request.getAmount() > 1000) { // Simulating insufficient funds
            throw new RuntimeException("Payment declined due to insufficient funds");
        }
        payment.setOrderId(request.getOrderId());
        payment.setAmount(request.getAmount());

        payment.setStatus("SUCCESS");

        paymentRepository.save(payment);
        return "Payment Processed Successfully for Order ID: " + request.getOrderId();
    }

    public String refundPayment(Long orderId) {
        Optional<Payment> paymentOpt = paymentRepository.findByOrderId(orderId);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus("REFUNDED");
            paymentRepository.save(payment);
            return "Payment Refunded for Order ID: " + orderId;
        }
        return "No payment found for Order ID: " + orderId;
    }
}