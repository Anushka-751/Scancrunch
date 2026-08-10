package com.scanCrunch.domain.menu.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.scanCrunch.domain.menu.dto.CategoryResponse;
import com.scanCrunch.domain.menu.dto.FeaturedFoodResponse;
import com.scanCrunch.domain.menu.dto.HomeResponse;
import com.scanCrunch.domain.menu.entity.Category;
import com.scanCrunch.domain.menu.entity.MenuItem;

public class HomeMapper {

    public static CategoryResponse mapCategory(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName());
    }

    public static FeaturedFoodResponse mapMenuItem(MenuItem menuItem) {
        return new FeaturedFoodResponse(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getImageUrl(),
                BigDecimal.valueOf(menuItem.getPrice()),
                menuItem.getRating(),
                menuItem.isAvailable());
    }

    public static HomeResponse mapHomeResponse(
            String restaurantName,
            String welcomeMessage,
            List<Category> categories,
            List<MenuItem> menuItems) {

        HomeResponse response = new HomeResponse();

        response.setRestaurantName(restaurantName);
        response.setWelcomeMessage(welcomeMessage);

        response.setCategories(
                categories.stream()
                        .map(HomeMapper::mapCategory)
                        .collect(Collectors.toList()));

        response.setFeaturedFoods(
                menuItems.stream()
                        .map(HomeMapper::mapMenuItem)
                        .collect(Collectors.toList()));

        return response;
    }
}

