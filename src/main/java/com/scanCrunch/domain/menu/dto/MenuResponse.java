package com.scanCrunch.domain.menu.dto;

import java.util.List;

public class MenuResponse {

    private String selectedCategory;
    private int totalItems;
    private List<FoodItemResponse> foodItems;

    public String getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(String selectedCategory) {
        this.selectedCategory = selectedCategory;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<FoodItemResponse> getFoodItems() {
        return foodItems;
    }

    public void setFoodItems(List<FoodItemResponse> foodItems) {
        this.foodItems = foodItems;
    }
}