
package com.scanCrunch.domain.cart.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.cart.dto.CartItemResponse;
import com.scanCrunch.domain.cart.dto.CartResponse;
import com.scanCrunch.domain.cart.entity.Cart;

@Component
public class CartMapper {

    public CartResponse toCartResponse(List<Cart> carts) {

        CartResponse response = new CartResponse();

        List<CartItemResponse> items = carts.stream()
                .map(this::toCartItemResponse)
                .toList();

        response.setItems(items);
        response.setTotalItems(items.size());

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        response.setTotalAmount(totalAmount);

        return response;
    }

    public CartItemResponse toCartItemResponse(Cart cart) {

        CartItemResponse item = new CartItemResponse();

        item.setId(cart.getId());
        item.setFoodItemId(cart.getFoodItem().getId());

        // Food Details
        item.setFoodName(cart.getFoodItem().getName());
        item.setImageUrl(cart.getFoodItem().getImageUrl());
        item.setDescription(cart.getFoodItem().getDescription());

        // Cart Details
        item.setUnitPrice(cart.getUnitPrice());
        item.setQuantity(cart.getQuantity());
        item.setSubtotal(cart.getSubtotal());

        return item;
    }
}
