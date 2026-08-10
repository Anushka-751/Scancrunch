package com.scanCrunch.domain.receipt.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptItemPayload {

    private String foodName;

    private int quantity;

    private String formattedPrice;

    private String formattedSubtotal;
}
