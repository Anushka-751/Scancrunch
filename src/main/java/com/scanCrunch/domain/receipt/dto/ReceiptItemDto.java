package com.scanCrunch.domain.receipt.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemDto {

    private String foodName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;
}
