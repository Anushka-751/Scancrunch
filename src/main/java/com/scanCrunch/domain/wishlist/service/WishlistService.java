package com.scanCrunch.domain.wishlist.service;

import java.util.List;

import com.scanCrunch.domain.wishlist.dto.WishlistResponse;

public interface WishlistService {

    WishlistResponse addToWishlist(Long menuId);

    List<WishlistResponse> getWishlist();

    void removeFromWishlist(Long wishlistId);

    boolean isWishlisted(Long menuId);

    void clearWishlist();
}
