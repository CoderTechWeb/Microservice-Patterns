package com.techweb.orderservice.data;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private Long orderId;
    private String status;
}
