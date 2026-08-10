package com.scanCrunch.domain.order.service;

import java.util.List;

import com.scanCrunch.domain.order.dto.OrderResponse;
import com.scanCrunch.domain.order.dto.OrderStatusResponse;

public interface OrderService {

    OrderStatusResponse placeOrder(String paymentMethod);

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(Long orderId);

    OrderStatusResponse updateOrderStatus(Long id, String status);

    default void cancelOrder(Long orderId) {
        updateOrderStatus(orderId, "CANCELLED");
    }
}