package com.scanCrunch.domain.menu.dto;



import java.util.List;

public class HomeResponse {

    private String restaurantName;
    private String welcomeMessage;
    private List<CategoryResponse> categories;
    private List<FeaturedFoodResponse> featuredFoods;

    public HomeResponse() {
    }

    public HomeResponse(String restaurantName, String welcomeMessage,
                        List<CategoryResponse> categories,
                        List<FeaturedFoodResponse> featuredFoods) {
        this.restaurantName = restaurantName;
        this.welcomeMessage = welcomeMessage;
        this.categories = categories;
        this.featuredFoods = featuredFoods;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryResponse> categories) {
        this.categories = categories;
    }

    public List<FeaturedFoodResponse> getFeaturedFoods() {
        return featuredFoods;
    }

    public void setFeaturedFoods(List<FeaturedFoodResponse> featuredFoods) {
        this.featuredFoods = featuredFoods;
    }
}

