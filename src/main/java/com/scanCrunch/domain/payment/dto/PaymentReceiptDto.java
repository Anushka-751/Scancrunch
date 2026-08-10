package com.scanCrunch.domain.payment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptDto {

    private String paymentId;

    private String emailStatus;

    private LocalDateTime sentAt;

    private Integer retryCount;
}
