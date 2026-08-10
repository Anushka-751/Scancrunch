package com.scanCrunch.domain.menu.dto;

public class SearchResponse {

    private Long id;
    private String name;
    private String imageUrl;
    private double rating;

    public SearchResponse() {
    }

    public SearchResponse(Long id, String name, double rating) {
        this.id = id;
        this.name = name;
        this.rating = rating;
    }

    public SearchResponse(Long id, String name, String imageUrl, double rating) {
        this(id, name, rating);
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}