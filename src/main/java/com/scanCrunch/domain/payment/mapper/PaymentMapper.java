package com.scanCrunch.domain.payment.mapper;

import com.scanCrunch.domain.payment.entity.Payment;

public class PaymentMapper {

    public Payment toEntity(Payment payment) {
        return payment;
    }

    public Object toDto(Payment payment) {
        return payment;
    }

    public Object toPaymentResponse(Payment payment) {
        return payment;
    }

    public Payment toPayment(Payment payment) {
        return payment;
    }
}