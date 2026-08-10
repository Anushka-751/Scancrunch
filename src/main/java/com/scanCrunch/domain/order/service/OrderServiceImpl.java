package com.scanCrunch.domain.order.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scanCrunch.core.exception.BadRequestException;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.core.security.SecurityUtils;
import com.scanCrunch.domain.cart.entity.Cart;
import com.scanCrunch.domain.cart.repository.CartRepository;
import com.scanCrunch.domain.order.dto.OrderResponse;
import com.scanCrunch.domain.order.dto.OrderStatusResponse;
import com.scanCrunch.domain.order.entity.Order;
import com.scanCrunch.domain.order.entity.OrderItem;
import com.scanCrunch.domain.order.mapper.OrderMapper;
import com.scanCrunch.domain.order.repository.OrderItemRepository;
import com.scanCrunch.domain.order.repository.OrderRepository;
import com.scanCrunch.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final CartRepository cartRepository;
    private final SecurityUtils securityUtils;

    // GST Percentage
    private static final BigDecimal GST_PERCENTAGE = BigDecimal.valueOf(5);

    private static final List<String> VALID_STATUS = List.of(
            "PENDING",
            "COMPLETED",
            "ACCEPTED",
            "PREPARING",
            "READY",
            "SERVED",
            "CANCELLED"
    );

    @Override
    public OrderStatusResponse placeOrder(String paymentMethod) {

        User currentUser = securityUtils.getCurrentUser();
        Long userId = currentUser.getId();

        List<Cart> carts = cartRepository.findByUser(currentUser);

        if (carts.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        // ==========================
        // Calculate Subtotal
        // ==========================
        BigDecimal subtotal = BigDecimal.ZERO;

        for (Cart cart : carts) {

            BigDecimal itemSubtotal = cart.getSubtotal() == null
                    ? BigDecimal.ZERO
                    : cart.getSubtotal();

            subtotal = subtotal.add(itemSubtotal);
        }

        // ==========================
        // Calculate GST (5%)
        // ==========================
        BigDecimal gstAmount = subtotal
                .multiply(GST_PERCENTAGE)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // ==========================
        // Final Total
        // ==========================
        BigDecimal totalAmount = subtotal.add(gstAmount);

        // ==========================
        // Create Order
        // ==========================
        Order order = new Order();

        order.setUserId(userId);
        order.setPaymentMethod(paymentMethod);
        order.setTotalAmount(totalAmount);
        order.setOrderedAt(LocalDateTime.now());

        // Cash -> Completed
        // UPI -> Pending
        if ("CASH".equalsIgnoreCase(paymentMethod)) {
            order.setStatus("COMPLETED");
        } else {
            order.setStatus("PENDING");
        }

        Order savedOrder = orderRepository.save(order);

        // ==========================
        // Save Order Items
        // ==========================
        for (Cart cart : carts) {

            OrderItem item = new OrderItem();

            item.setOrderId(savedOrder.getId());

            item.setFoodItemId(cart.getFoodItem().getId());

            item.setFoodName(cart.getFoodItem().getName());

            item.setQuantity(cart.getQuantity());

            item.setPrice(cart.getUnitPrice());

            item.setSubtotal(cart.getSubtotal());

            orderItemRepository.save(item);
        }

        // ==========================
        // Clear Cart
        // ==========================
        cartRepository.deleteAll(carts);

        return new OrderStatusResponse(
                savedOrder.getId(),
                savedOrder.getStatus());
    }

    @Override
    public List<OrderResponse> getAllOrders() {

        User currentUser = securityUtils.getCurrentUser();

        List<Order> orders =
                orderRepository.findByUserIdOrderByOrderedAtDesc(currentUser.getId());

        return orders.stream()
                .map(order -> {
                    List<OrderItem> items =
                            orderItemRepository.findByOrderId(order.getId());

                    return orderMapper.toOrderResponse(order, items);
                })
                .toList();
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        List<OrderItem> items =
                orderItemRepository.findByOrderId(order.getId());

        return orderMapper.toOrderResponse(order, items);
    }

    @Override
    public OrderStatusResponse updateOrderStatus(Long id, String status) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        String normalizedStatus = status.toUpperCase();

        if (!VALID_STATUS.contains(normalizedStatus)) {
            throw new BadRequestException("Invalid Order Status");
        }

        String current = order.getStatus();

        if (current.equals("PENDING")
                && !(normalizedStatus.equals("COMPLETED")
                || normalizedStatus.equals("ACCEPTED")
                || normalizedStatus.equals("CANCELLED"))) {

            throw new BadRequestException("Invalid Status Transition");
        }

        if (current.equals("ACCEPTED")
                && !normalizedStatus.equals("PREPARING")) {

            throw new BadRequestException("Invalid Status Transition");
        }

        if (current.equals("PREPARING")
                && !normalizedStatus.equals("READY")) {

            throw new BadRequestException("Invalid Status Transition");
        }

        if (current.equals("READY")
                && !normalizedStatus.equals("SERVED")) {

            throw new BadRequestException("Invalid Status Transition");
        }

        order.setStatus(normalizedStatus);

        orderRepository.save(order);

        return new OrderStatusResponse(
                order.getId(),
                order.getStatus());
    }

    @Override
    public void cancelOrder(Long orderId) {
        updateOrderStatus(orderId, "CANCELLED");
    }
}