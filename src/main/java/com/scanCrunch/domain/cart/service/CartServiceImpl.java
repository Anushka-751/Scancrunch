package com.scanCrunch.domain.cart.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.scanCrunch.core.exception.BadRequestException;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.core.security.SecurityUtils;
import com.scanCrunch.domain.cart.dto.AddToCartRequest;
import com.scanCrunch.domain.cart.dto.ApiResponse;
import com.scanCrunch.domain.cart.dto.CartResponse;
import com.scanCrunch.domain.cart.dto.UpdateCartRequest;
import com.scanCrunch.domain.cart.entity.Cart;
import com.scanCrunch.domain.cart.mapper.CartMapper;
import com.scanCrunch.domain.cart.repository.CartRepository;
import com.scanCrunch.domain.menu.entity.MenuItem;
import com.scanCrunch.domain.menu.repository.MenuRepository;
import com.scanCrunch.domain.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final CartMapper cartMapper;
    private final SecurityUtils securityUtils;

    @Override
    public ApiResponse addToCart(AddToCartRequest request) {

        User currentUser = securityUtils.getCurrentUser();

        if (currentUser == null) {
            throw new BadRequestException("User not authenticated");
        }

        MenuItem menuItem = menuRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));

        if (!menuItem.isAvailable()) {
            throw new BadRequestException("Food item is not available");
        }

        Cart cart = cartRepository
                .findByUserAndFoodItem(currentUser, menuItem)
                .orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(currentUser);
            cart.setFoodItem(menuItem);
            cart.setQuantity(request.getQuantity());

            BigDecimal price = BigDecimal.valueOf(menuItem.getPrice());

            cart.setUnitPrice(price);
            cart.setSubtotal(price.multiply(BigDecimal.valueOf(request.getQuantity())));
        } else {
            int quantity = cart.getQuantity() + request.getQuantity();
            cart.setQuantity(quantity);
            cart.setSubtotal(
                    cart.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        cartRepository.save(cart);

        return new ApiResponse(true, "Item added to cart successfully");
    }

    @Override
    public CartResponse getCart() {

        User currentUser = securityUtils.getCurrentUser();

        if (currentUser == null) {
            throw new BadRequestException("User not authenticated");
        }

        List<Cart> cartItems = cartRepository.findByUser(currentUser);

        System.out.println("==============================");
        System.out.println("Cart Size : " + cartItems.size());

        for (Cart cart : cartItems) {
            System.out.println(
                    "CartId=" + cart.getId()
                    + " Food=" + cart.getFoodItem().getName()
                    + " Qty=" + cart.getQuantity()
            );
        }

        System.out.println("==============================");

        if (cartItems.isEmpty()) {

            CartResponse response = new CartResponse();
            response.setItems(List.of());
            response.setTotalItems(0);
            response.setTotalAmount(BigDecimal.ZERO);

            return response;
        }

        return cartMapper.toCartResponse(cartItems);
    }

    @Override
    public ApiResponse updateCart(Long id, UpdateCartRequest request) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        cart.setQuantity(request.getQuantity());
        cart.setSubtotal(
                cart.getUnitPrice().multiply(BigDecimal.valueOf(request.getQuantity())));

        cartRepository.save(cart);

        return new ApiResponse(true, "Cart updated successfully");
    }

    @Override
    public ApiResponse removeCart(Long id) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        cartRepository.delete(cart);

        return new ApiResponse(true, "Item removed successfully");
    }

    @Override
    public void clearCart() {

        User currentUser = securityUtils.getCurrentUser();

        if (currentUser != null) {
            cartRepository.deleteByUser(currentUser);
        }
    }
}
