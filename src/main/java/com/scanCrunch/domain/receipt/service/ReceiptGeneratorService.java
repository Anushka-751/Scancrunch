package com.scanCrunch.domain.receipt.service;

import com.scanCrunch.domain.payment.entity.Payment;
import com.scanCrunch.domain.receipt.dto.ReceiptDto;

public interface ReceiptGeneratorService {

    /**
     * Assembles a {@link ReceiptDto} for the given payment by pulling the
     * associated order, order items and customer details.
     */
    ReceiptDto generateReceipt(Payment payment);
}
