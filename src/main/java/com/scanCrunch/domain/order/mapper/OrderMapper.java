package com.scanCrunch.domain.order.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.order.dto.OrderItemResponse;
import com.scanCrunch.domain.order.dto.OrderResponse;
import com.scanCrunch.domain.order.entity.Order;
import com.scanCrunch.domain.order.entity.OrderItem;

@Component
public class OrderMapper {

    public OrderResponse toOrderResponse(Order order, List<OrderItem> items) {

        OrderResponse response = new OrderResponse();

        response.setOrderId(order.getId());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setOrderedAt(order.getOrderedAt());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());

        response.setItems(itemResponses);

        return response;
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {

        OrderItemResponse response = new OrderItemResponse();

        response.setFoodName(item.getFoodName());
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        response.setSubtotal(item.getSubtotal());

        return response;
    }

}