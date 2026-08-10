package com.scanCrunch.domain.menu.dto;

import java.math.BigDecimal;

public class FeaturedFoodResponse {

    private Long id;
    private String name;
    private String image;
    private BigDecimal price;
    private Double rating;
    private boolean available;

    public FeaturedFoodResponse() {
    }

    public FeaturedFoodResponse(Long id, String name, String image,
            BigDecimal price, Double rating, boolean available) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.rating = rating;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
