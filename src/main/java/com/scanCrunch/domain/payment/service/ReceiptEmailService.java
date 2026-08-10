package com.scanCrunch.domain.payment.service;

import com.scanCrunch.domain.payment.dto.PaymentReceiptDto;

public interface ReceiptEmailService {

    /**
     * Sends the payment receipt email for the given (external) payment id.
     * Only sends once per payment — a second call is a no-op signalled via
     * {@link com.scanCrunch.core.exception.DuplicateReceiptException}.
     */
    void sendReceipt(String paymentId);

    /**
     * Force re-sends the receipt email regardless of previous send status.
     */
    void resendReceipt(String paymentId);

    PaymentReceiptDto getEmailStatus(String paymentId);
}
