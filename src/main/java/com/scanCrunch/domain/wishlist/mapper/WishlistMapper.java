package com.scanCrunch.domain.wishlist.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.menu.entity.MenuItem;
import com.scanCrunch.domain.wishlist.dto.WishlistResponse;
import com.scanCrunch.domain.wishlist.entity.Wishlist;

@Component
public class WishlistMapper {

    private static final String DEFAULT_RESTAURANT_NAME = "ScanCrunch Restaurant";

    public WishlistResponse toWishlistResponse(Wishlist wishlist) {

        WishlistResponse response = new WishlistResponse();
        MenuItem menuItem = wishlist.getMenu();

        response.setWishlistId(wishlist.getId());
        response.setMenuId(menuItem.getId());
        response.setName(menuItem.getName());
        response.setDescription(menuItem.getDescription());
        response.setPrice(menuItem.getPrice());
        response.setImageUrl(menuItem.getImageUrl());
        response.setRestaurantName(DEFAULT_RESTAURANT_NAME);
        response.setAvailable(menuItem.isAvailable());
        response.setRating(menuItem.getRating());

        return response;
    }

    public List<WishlistResponse> toWishlistResponseList(List<Wishlist> wishlists) {

        return wishlists.stream()
                .map(this::toWishlistResponse)
                .toList();
    }
}
