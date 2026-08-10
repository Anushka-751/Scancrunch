package com.scanCrunch.domain.receipt.mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.order.entity.Order;
import com.scanCrunch.domain.order.entity.OrderItem;
import com.scanCrunch.domain.payment.entity.Payment;
import com.scanCrunch.domain.receipt.dto.ReceiptDto;
import com.scanCrunch.domain.receipt.dto.ReceiptItemDto;
import com.scanCrunch.domain.receipt.payload.ReceiptItemPayload;
import com.scanCrunch.domain.receipt.payload.ReceiptPayload;
import com.scanCrunch.domain.user.entity.User;

@Component
public class ReceiptMapper {

    private static final String RESTAURANT_NAME = "ScanCrunch";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a");

    public ReceiptDto toReceiptDto(Payment payment, Order order, List<OrderItem> orderItems, User user) {

        ReceiptDto dto = new ReceiptDto();

        dto.setRestaurantName(RESTAURANT_NAME);
        dto.setCustomerName(user.getFirstName() + " " + user.getLastName());
        dto.setCustomerEmail(user.getEmail());
        dto.setOrderId(order.getId());
        dto.setPaymentId(payment.getRazorpayPaymentId());
        dto.setOrderDate(order.getOrderedAt());
        dto.setPaymentDate(payment.getPaidAt());
        dto.setTotalAmount(payment.getAmount() != null ? payment.getAmount() : order.getTotalAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());

        dto.setItems(orderItems.stream()
                .map(item -> new ReceiptItemDto(
                        item.getFoodName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSubtotal()))
                .toList());

        return dto;
    }

    public ReceiptPayload toReceiptPayload(ReceiptDto dto) {

        ReceiptPayload payload = new ReceiptPayload();

        payload.setRestaurantName(dto.getRestaurantName());
        payload.setCustomerName(dto.getCustomerName());
        payload.setCustomerEmail(dto.getCustomerEmail());
        payload.setOrderId("ORD" + String.format("%06d", dto.getOrderId()));
        payload.setPaymentId(dto.getPaymentId());

        payload.setOrderDate(dto.getOrderDate() != null
                ? dto.getOrderDate().format(DATE_FORMATTER)
                : "-");

        payload.setPaymentDate(dto.getPaymentDate() != null
                ? dto.getPaymentDate().format(DATE_FORMATTER) + " " + dto.getPaymentDate().format(TIME_FORMATTER)
                : "-");

        payload.setFormattedTotalAmount("\u20B9" + dto.getTotalAmount());
        payload.setPaymentMethod(dto.getPaymentMethod());
        payload.setPaymentStatus(dto.getPaymentStatus());

        payload.setItems(dto.getItems().stream()
                .map(item -> new ReceiptItemPayload(
                        item.getFoodName(),
                        item.getQuantity(),
                        "\u20B9" + item.getPrice(),
                        "\u20B9" + item.getSubtotal()))
                .toList());

        return payload;
    }
}
