package com.scanCrunch.domain.receipt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanCrunch.core.exception.ReceiptGenerationException;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.domain.order.entity.Order;
import com.scanCrunch.domain.order.entity.OrderItem;
import com.scanCrunch.domain.order.repository.OrderItemRepository;
import com.scanCrunch.domain.order.repository.OrderRepository;
import com.scanCrunch.domain.payment.entity.Payment;
import com.scanCrunch.domain.receipt.dto.ReceiptDto;
import com.scanCrunch.domain.receipt.mapper.ReceiptMapper;
import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReceiptGeneratorServiceImpl implements ReceiptGeneratorService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ReceiptMapper receiptMapper;

    @Override
    public ReceiptDto generateReceipt(Payment payment) {

        if (payment == null) {
            throw new ReceiptGenerationException("Cannot generate receipt: payment is missing");
        }

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found for payment " + payment.getRazorpayPaymentId()));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        if (orderItems.isEmpty()) {
            throw new ReceiptGenerationException(
                    "Cannot generate receipt: no items found for order " + order.getId());
        }

        User user = userRepository.findById(order.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for order " + order.getId()));

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ReceiptGenerationException(
                    "Cannot send receipt: customer email is missing for order " + order.getId());
        }

        return receiptMapper.toReceiptDto(payment, order, orderItems, user);
    }
}
