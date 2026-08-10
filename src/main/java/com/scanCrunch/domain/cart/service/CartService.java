package com.scanCrunch.domain.cart.service;

import com.scanCrunch.domain.cart.dto.AddToCartRequest;
import com.scanCrunch.domain.cart.dto.ApiResponse;
import com.scanCrunch.domain.cart.dto.CartResponse;
import com.scanCrunch.domain.cart.dto.UpdateCartRequest;

public interface CartService {

    ApiResponse addToCart(AddToCartRequest request);

    CartResponse getCart();

    ApiResponse updateCart(Long id, UpdateCartRequest request);

    ApiResponse removeCart(Long id);

    void clearCart();
}