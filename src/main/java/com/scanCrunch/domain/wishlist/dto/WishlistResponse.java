package com.scanCrunch.domain.wishlist.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WishlistResponse {

    private Long wishlistId;
    private Long menuId;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private String restaurantName;
    private Boolean available;
    private Double rating;
}
