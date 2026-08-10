package com.scanCrunch.domain.payment.service;

import com.scanCrunch.domain.payment.dto.CreatePaymentRequest;
import com.scanCrunch.domain.payment.dto.PaymentResponse;
import com.scanCrunch.domain.payment.dto.PaymentSummaryResponse;
import com.scanCrunch.domain.payment.dto.VerifyPaymentRequest;

public interface PaymentService {

    PaymentResponse createPayment(CreatePaymentRequest request);

    String verifyPayment(VerifyPaymentRequest request);

    PaymentSummaryResponse getPaymentSummary(Long orderId);
}