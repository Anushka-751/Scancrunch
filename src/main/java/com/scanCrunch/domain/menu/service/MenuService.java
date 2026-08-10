package com.scanCrunch.domain.menu.service;

import java.util.List;

import com.scanCrunch.domain.menu.dto.CategoryResponse;
import com.scanCrunch.domain.menu.dto.FoodItemResponse;
import com.scanCrunch.domain.menu.dto.MenuResponse;
import com.scanCrunch.domain.menu.dto.SearchResponse;

public interface MenuService {

    // Get all categories
    List<CategoryResponse> getAllCategories();

    // Get all available food items
    List<FoodItemResponse> getAllFoodItems();

    // Get food items by category
    MenuResponse getFoodItemsByCategory(String category);

    // Search food items
    List<SearchResponse> searchFoodItems(String keyword);

}
