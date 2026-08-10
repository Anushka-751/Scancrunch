package com.scanCrunch.core.util;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.payment.entity.Payment;
import com.scanCrunch.domain.receipt.dto.ReceiptDto;
import com.scanCrunch.domain.receipt.mapper.ReceiptMapper;
import com.scanCrunch.domain.receipt.payload.ReceiptPayload;
import com.scanCrunch.domain.receipt.service.ReceiptGeneratorService;

import lombok.RequiredArgsConstructor;

/**
 * Ties together {@link ReceiptGeneratorService}, {@link ReceiptMapper} and
 * {@link HtmlReceiptGenerator} to turn a {@link Payment} into a finished
 * HTML email body, ready to send.
 */
@Component
@RequiredArgsConstructor
public class ReceiptTemplateBuilder {

    private final ReceiptGeneratorService receiptGeneratorService;
    private final ReceiptMapper receiptMapper;
    private final HtmlReceiptGenerator htmlReceiptGenerator;

    public String buildHtml(Payment payment) {
        return buildHtmlFromDto(buildReceiptDto(payment));
    }

    public ReceiptDto buildReceiptDto(Payment payment) {
        return receiptGeneratorService.generateReceipt(payment);
    }

    public String buildHtmlFromDto(ReceiptDto receiptDto) {

        ReceiptPayload payload = receiptMapper.toReceiptPayload(receiptDto);

        return htmlReceiptGenerator.generate(payload);
    }
}
