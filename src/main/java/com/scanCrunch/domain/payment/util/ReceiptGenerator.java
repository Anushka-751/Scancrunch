package com.scanCrunch.domain.payment.util;

import com.scanCrunch.domain.payment.entity.Payment;

public class ReceiptGenerator {

        public String generate(Payment payment) {
                return "Receipt generated";
        }

        public String generateReceipt(Payment payment) {
                return generate(payment);
        }

        public String createReceipt(Payment payment) {
                return generate(payment);
        }

        public String buildReceipt(Payment payment) {
                return generate(payment);
        }
}