package com.scanCrunch.domain.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.scanCrunch.domain.cart.dto.AddToCartRequest;
import com.scanCrunch.domain.cart.dto.ApiResponse;
import com.scanCrunch.domain.cart.dto.CartResponse;
import com.scanCrunch.domain.cart.dto.UpdateCartRequest;
import com.scanCrunch.domain.cart.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Add item to cart
    @PostMapping
    public ApiResponse addToCart(
            @Valid @RequestBody AddToCartRequest request) {

        return cartService.addToCart(request);
    }

    // Get cart
    @GetMapping
    public CartResponse getCart() {

        return cartService.getCart();
    }

    // Update cart quantity
    @PutMapping("/{id}")
    public ApiResponse updateCart(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartRequest request) {

        return cartService.updateCart(id, request);
    }

    // Remove item from cart
    @DeleteMapping("/{id}")
    public ApiResponse removeCart(
            @PathVariable Long id) {

        return cartService.removeCart(id);
    }
}
