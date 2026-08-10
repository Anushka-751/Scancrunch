package com.scanCrunch.domain.receipt.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Raw data assembled from the Order, Payment and User modules. Dates and
 * amounts are kept in their native types here; {@link ReceiptMapper} converts
 * this into a display-ready {@code ReceiptPayload} for the email template.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDto {

    private String restaurantName;

    private String customerName;

    private String customerEmail;

    private Long orderId;

    private String paymentId;

    private LocalDateTime orderDate;

    private LocalDateTime paymentDate;

    private List<ReceiptItemDto> items;

    private BigDecimal totalAmount;

    private String paymentMethod;

    private String paymentStatus;
}
