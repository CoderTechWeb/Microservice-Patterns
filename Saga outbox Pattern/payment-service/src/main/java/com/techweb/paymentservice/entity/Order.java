package com.techweb.paymentservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String product;
    private int quantity;
    private String status;
}