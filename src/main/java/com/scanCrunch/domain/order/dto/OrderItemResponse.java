package com.scanCrunch.domain.order.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {

    private String foodName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;

}