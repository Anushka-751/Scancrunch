package com.scanCrunch.domain.payment.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scanCrunch.core.exception.DuplicateReceiptException;
import com.scanCrunch.core.exception.EmailDeliveryException;
import com.scanCrunch.core.exception.PaymentException;
import com.scanCrunch.core.exception.ReceiptNotFoundException;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.core.util.EmailRetryUtil;
import com.scanCrunch.core.util.ReceiptEmailSender;
import com.scanCrunch.core.util.ReceiptTemplateBuilder;
import com.scanCrunch.domain.payment.dto.PaymentReceiptDto;
import com.scanCrunch.domain.payment.entity.Payment;
import com.scanCrunch.domain.payment.repository.PaymentRepository;
import com.scanCrunch.domain.receipt.dto.ReceiptDto;
import com.scanCrunch.domain.receipt.entity.PaymentReceipt;
import com.scanCrunch.domain.receipt.enums.EmailStatus;
import com.scanCrunch.domain.receipt.repository.PaymentReceiptRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceiptEmailServiceImpl implements ReceiptEmailService {

    private final PaymentRepository paymentRepository;
    private final PaymentReceiptRepository paymentReceiptRepository;
    private final ReceiptTemplateBuilder receiptTemplateBuilder;
    private final ReceiptEmailSender receiptEmailSender;
    private final EmailRetryUtil emailRetryUtil;

    @Value("${receipt.email.max-retry-attempts:3}")
    private int maxRetryAttempts;

    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String EMAIL_SUBJECT_PREFIX = "Payment Receipt - Order ";

    @Override
    @Transactional
    public void sendReceipt(String paymentId) {

        Payment payment = findPaymentOrThrow(paymentId);

        validatePaymentIsSuccessful(payment);

        PaymentReceipt existing = paymentReceiptRepository.findByPaymentId(paymentId).orElse(null);

        if (existing != null && existing.getEmailStatus() == EmailStatus.SENT) {
            throw new DuplicateReceiptException(
                    "Receipt email already sent for payment " + paymentId);
        }

        dispatch(payment, existing);
    }

    @Override
    @Transactional
    public void resendReceipt(String paymentId) {

        Payment payment = findPaymentOrThrow(paymentId);

        validatePaymentIsSuccessful(payment);

        PaymentReceipt existing = paymentReceiptRepository.findByPaymentId(paymentId).orElse(null);

        dispatch(payment, existing);
    }

    @Override
    public PaymentReceiptDto getEmailStatus(String paymentId) {

        PaymentReceipt receipt = paymentReceiptRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ReceiptNotFoundException(
                        "No receipt record found for payment " + paymentId));

        return new PaymentReceiptDto(
                receipt.getPaymentId(),
                receipt.getEmailStatus().name(),
                receipt.getSentAt(),
                receipt.getRetryCount());
    }

    private void dispatch(Payment payment, PaymentReceipt existing) {

        ReceiptDto receiptDto = receiptTemplateBuilder.buildReceiptDto(payment);
        String html = receiptTemplateBuilder.buildHtmlFromDto(receiptDto);

        String subject = EMAIL_SUBJECT_PREFIX + receiptDto.getOrderId();
        String recipient = receiptDto.getCustomerEmail();

        int attemptsMade = emailRetryUtil.executeWithRetry(
                () -> receiptEmailSender.send(recipient, subject, html),
                maxRetryAttempts);

        PaymentReceipt receipt = existing != null ? existing : new PaymentReceipt();

        receipt.setPaymentId(payment.getRazorpayPaymentId());
        receipt.setOrderId(String.valueOf(payment.getOrderId()));
        receipt.setEmail(recipient);

        if (attemptsMade > 0) {

            receipt.setEmailStatus(EmailStatus.SENT);
            receipt.setRetryCount(attemptsMade);
            receipt.setSentAt(LocalDateTime.now());

            paymentReceiptRepository.save(receipt);

        } else {

            receipt.setEmailStatus(EmailStatus.FAILED);
            receipt.setRetryCount(-attemptsMade);

            paymentReceiptRepository.save(receipt);

            log.error("Payment receipt email delivery failed after {} attempts for payment {}",
                    -attemptsMade, payment.getRazorpayPaymentId());

            throw new EmailDeliveryException(
                    "Failed to send receipt email after " + (-attemptsMade) + " attempts");
        }
    }

    private Payment findPaymentOrThrow(String paymentId) {

        return paymentRepository.findByRazorpayPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found: " + paymentId));
    }

    private void validatePaymentIsSuccessful(Payment payment) {

        if (payment.getPaymentStatus() == null
                || !SUCCESS_STATUS.equalsIgnoreCase(payment.getPaymentStatus())) {

            throw new PaymentException(
                    "Receipt can only be sent for successful payments");
        }
    }
}
