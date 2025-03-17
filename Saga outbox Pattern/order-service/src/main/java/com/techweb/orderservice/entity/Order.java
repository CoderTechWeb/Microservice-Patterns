package com.techweb.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "outboxOrders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String product;
    private int quantity;
    private String status;
}