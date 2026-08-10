
package com.scanCrunch.domain.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.core.exception.DuplicateReceiptException;
import com.scanCrunch.domain.payment.dto.ApiResponse;
import com.scanCrunch.domain.payment.dto.CreatePaymentRequest;
import com.scanCrunch.domain.payment.dto.PaymentReceiptDto;
import com.scanCrunch.domain.payment.dto.PaymentResponse;
import com.scanCrunch.domain.payment.dto.PaymentSummaryResponse;
import com.scanCrunch.domain.payment.dto.VerifyPaymentRequest;
import com.scanCrunch.domain.payment.payload.PaymentReceiptRequest;
import com.scanCrunch.domain.payment.payload.PaymentReceiptResponse;
import com.scanCrunch.domain.payment.payload.ResendReceiptRequest;
import com.scanCrunch.domain.payment.service.PaymentService;
import com.scanCrunch.domain.payment.service.ReceiptEmailService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final ReceiptEmailService receiptEmailService;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Payment initiated successfully",
                        response
                )
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request) {

        String message = paymentService.verifyPayment(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        message,
                        null
                )
        );
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentSummaryResponse> getPaymentSummary(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.getPaymentSummary(orderId)
        );
    }

    @PostMapping("/send-receipt")
    public ResponseEntity<PaymentReceiptResponse> sendReceipt(
            @Valid @RequestBody PaymentReceiptRequest request) {

        try {
            receiptEmailService.sendReceipt(request.getPaymentId());

            return ResponseEntity.ok(
                    new PaymentReceiptResponse(
                            true,
                            "Payment receipt sent successfully."
                    )
            );

        } catch (DuplicateReceiptException ex) {

            return ResponseEntity.ok(
                    new PaymentReceiptResponse(
                            true,
                            "Payment receipt was already sent previously."
                    )
            );
        }
    }

    @PostMapping("/resend-receipt")
    public ResponseEntity<PaymentReceiptResponse> resendReceipt(
            @Valid @RequestBody ResendReceiptRequest request) {

        receiptEmailService.resendReceipt(request.getPaymentId());

        return ResponseEntity.ok(
                new PaymentReceiptResponse(
                        true,
                        "Receipt resent successfully."
                )
        );
    }

    @GetMapping("/email-status/{paymentId}")
    public ResponseEntity<PaymentReceiptDto> getEmailStatus(
            @PathVariable String paymentId) {

        return ResponseEntity.ok(
                receiptEmailService.getEmailStatus(paymentId)
        );
    }
}

