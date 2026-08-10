package com.scanCrunch.domain.receipt.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Fully formatted, display-ready version of {@code ReceiptDto} — every
 * value here is already a String suitable for direct insertion into the
 * HTML email template (currency formatted, dates formatted, etc).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptPayload {

    private String restaurantName;

    private String customerName;

    private String customerEmail;

    private String orderId;

    private String paymentId;

    private String orderDate;

    private String paymentDate;

    private List<ReceiptItemPayload> items;

    private String formattedTotalAmount;

    private String paymentMethod;

    private String paymentStatus;
}
