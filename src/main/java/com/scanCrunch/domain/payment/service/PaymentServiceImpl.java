package com.scanCrunch.domain.payment.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.scanCrunch.core.exception.PaymentException;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.domain.order.entity.Order;
import com.scanCrunch.domain.order.repository.OrderRepository;
import com.scanCrunch.domain.payment.dto.CreatePaymentRequest;
import com.scanCrunch.domain.payment.dto.PaymentResponse;
import com.scanCrunch.domain.payment.dto.PaymentSummaryResponse;
import com.scanCrunch.domain.payment.dto.VerifyPaymentRequest;
import com.scanCrunch.domain.payment.entity.Payment;
import com.scanCrunch.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptEmailService receiptEmailService;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        try {

            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            JSONObject options = new JSONObject();

            BigDecimal totalAmount = order.getTotalAmount()
                    .setScale(2, RoundingMode.HALF_UP);

            long amountInPaise = totalAmount
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            options.put("amount", amountInPaise);
            options.put("currency", "INR");
            options.put("receipt", "ORDER_" + order.getId());

            com.razorpay.Order razorpayOrder =
                    razorpayClient.orders.create(options);

            return new PaymentResponse(
                    order.getId(),
                    razorpayOrder.get("id"),
                    keyId,
                    totalAmount.doubleValue(),
                    "INR",
                    "CREATED"
            );

        } catch (Exception e) {
            throw new PaymentException(
                    "Unable to create Razorpay order: " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public String verifyPayment(VerifyPaymentRequest request) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        try {

            JSONObject attributes = new JSONObject();

            attributes.put("razorpay_order_id", request.getRazorpayOrderId());
            attributes.put("razorpay_payment_id", request.getRazorpayPaymentId());
            attributes.put("razorpay_signature", request.getRazorpaySignature());

            boolean isValidSignature =
                    Utils.verifyPaymentSignature(attributes, keySecret);

            if (!isValidSignature) {
                throw new PaymentException(
                        "Payment signature verification failed"
                );
            }

        } catch (PaymentException pe) {
            throw pe;
        } catch (Exception e) {
            throw new PaymentException(
                    "Unable to verify payment: " + e.getMessage(),
                    e
            );
        }

        Payment payment = paymentRepository.findByOrderId(order.getId())
                .orElseGet(Payment::new);

        payment.setOrderId(order.getId());
        payment.setRazorpayOrderId(request.getRazorpayOrderId());
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setRazorpaySignature(request.getRazorpaySignature());
        payment.setPaymentMethod(order.getPaymentMethod());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentStatus("SUCCESS");
        payment.setPaidAt(LocalDateTime.now());

        paymentRepository.save(payment);

        order.setStatus("COMPLETED");
        orderRepository.save(order);

        // Receipt email must never block or fail the payment verification itself.
        try {
            receiptEmailService.sendReceipt(payment.getRazorpayPaymentId());
        } catch (Exception e) {
            log.warn(
                    "Payment {} verified but receipt email could not be sent: {}",
                    payment.getRazorpayPaymentId(),
                    e.getMessage()
            );
        }

        return "VERIFIED";
    }

    @Override
    public PaymentSummaryResponse getPaymentSummary(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        return new PaymentSummaryResponse(
                order.getId(),
                order.getTotalAmount().doubleValue(),
                order.getStatus()
        );
    }
}