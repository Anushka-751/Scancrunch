package com.scanCrunch.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long orderId;

    private String razorpayOrderId;

    private String key;

    private Double amount;

    private String currency;

    private String status;

}