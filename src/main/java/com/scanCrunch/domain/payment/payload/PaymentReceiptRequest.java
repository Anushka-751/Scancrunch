package com.scanCrunch.domain.payment.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReceiptRequest {

    @NotBlank(message = "Payment Id is required")
    private String paymentId;
}
