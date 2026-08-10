package com.scanCrunch.domain.wishlist.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.domain.wishlist.dto.ApiResponse;
import com.scanCrunch.domain.wishlist.dto.WishlistRequest;
import com.scanCrunch.domain.wishlist.dto.WishlistResponse;
import com.scanCrunch.domain.wishlist.dto.WishlistStatusResponse;
import com.scanCrunch.domain.wishlist.service.WishlistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // POST /api/v1/wishlist
    @PostMapping
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @Valid @RequestBody WishlistRequest request) {

        WishlistResponse response = wishlistService.addToWishlist(request.getMenuId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Product added to wishlist", response));
    }

    // GET /api/v1/wishlist
    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getWishlist() {

        List<WishlistResponse> response = wishlistService.getWishlist();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Wishlist fetched successfully", response));
    }

    // DELETE /api/v1/wishlist/{wishlistId}
    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<ApiResponse<Void>> removeFromWishlist(
            @PathVariable Long wishlistId) {

        wishlistService.removeFromWishlist(wishlistId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Product removed from wishlist"));
    }

    // GET /api/v1/wishlist/check/{menuId}
    @GetMapping("/check/{menuId}")
    public ResponseEntity<WishlistStatusResponse> checkWishlistStatus(
            @PathVariable Long menuId) {

        boolean wishlisted = wishlistService.isWishlisted(menuId);

        return ResponseEntity.ok(new WishlistStatusResponse(true, wishlisted));
    }

    // DELETE /api/v1/wishlist
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearWishlist() {

        wishlistService.clearWishlist();

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Wishlist cleared successfully"));
    }
}
