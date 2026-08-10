package com.scanCrunch.domain.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.domain.order.dto.ApiResponse;
import com.scanCrunch.domain.order.dto.OrderResponse;
import com.scanCrunch.domain.order.dto.OrderStatusResponse;
import com.scanCrunch.domain.order.dto.PlaceOrderRequest;
import com.scanCrunch.domain.order.dto.UpdateOrderStatusRequest;
import com.scanCrunch.domain.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Place Order
    @PostMapping
    public ResponseEntity<ApiResponse<OrderStatusResponse>> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request) {

        OrderStatusResponse response =
                orderService.placeOrder(request.getPaymentMethod());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(
                        true,
                        "Order placed successfully",
                        response));
    }

    // Update Order Status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderStatusResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        OrderStatusResponse response =
                orderService.updateOrderStatus(id, request.getStatus());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Order status updated successfully",
                        response));
    }

    // Get All Orders
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {

        List<OrderResponse> orders = orderService.getAllOrders();

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Orders retrieved successfully",
                        orders));
    }

    // Get Order By Id
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id) {

        OrderResponse order = orderService.getOrderById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Order retrieved successfully",
                        order));
    }

    // Cancel Order
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(
            @PathVariable Long id) {

        orderService.cancelOrder(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Order cancelled successfully",
                        null));
    }
}