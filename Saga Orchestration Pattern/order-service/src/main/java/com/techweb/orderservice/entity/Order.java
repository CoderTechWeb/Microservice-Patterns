package com.techweb.orderservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String product;
    private Integer quantity;
    private Double price;
    private String status;  // PENDING, COMPLETED, FAILED
}