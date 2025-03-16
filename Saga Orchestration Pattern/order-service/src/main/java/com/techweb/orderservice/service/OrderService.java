package com.techweb.orderservice.service;

import com.techweb.orderservice.dto.InventoryRequest;
import com.techweb.orderservice.dto.PaymentRequest;
import com.techweb.orderservice.entity.Order;
import com.techweb.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    public String processOrder(Order order) {
        order.setStatus("PENDING");
        orderRepository.save(order);

        // API Gateway URL
        String API_GATEWAY_URL = "http://localhost:9000/";
        try {
            // Step 1: Process Payment
            restTemplate.postForEntity(API_GATEWAY_URL + "/payment/process",
                new PaymentRequest(order.getId(), order.getPrice()), String.class);

            // Step 2: Reserve Inventory
            restTemplate.postForEntity(API_GATEWAY_URL + "/inventory/reserve",
                new InventoryRequest(order.getId(), order.getProduct(), order.getQuantity()), String.class);

            // Step 3: Mark Order as Completed
            order.setStatus("COMPLETED");
        } catch (Exception e) {
            order.setStatus("FAILED");

            // Compensating Action: Refund Payment
            restTemplate.postForEntity(API_GATEWAY_URL + "/payment/refund?orderId=" + order.getId(),
                null, String.class);
        }

        orderRepository.save(order);
        return "Order Processed with status: " + order.getStatus();
    }
}