package com.scanCrunch.domain.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {

    private Long orderId;

    private List<OrderItemResponse> items;

    private BigDecimal totalAmount;

    private String paymentMethod;

    private String status;

    private LocalDateTime orderedAt;

}