package com.scanCrunch.domain.menu.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.scanCrunch.domain.menu.dto.CategoryResponse;
import com.scanCrunch.domain.menu.dto.FoodItemResponse;
import com.scanCrunch.domain.menu.dto.MenuResponse;
import com.scanCrunch.domain.menu.entity.Category;
import com.scanCrunch.domain.menu.entity.MenuItem;

@Component
public class MenuMapper {

    // Convert MenuItem Entity -> FoodItemResponse DTO
    public FoodItemResponse toFoodItemResponse(MenuItem menuItem) {

        FoodItemResponse response = new FoodItemResponse();

        response.setId(menuItem.getId());
        response.setName(menuItem.getName());
        response.setDescription(menuItem.getDescription());
        response.setImage(menuItem.getImageUrl());
        response.setPrice(menuItem.getPrice());
        response.setRating(menuItem.getRating());
        response.setAvailable(menuItem.isAvailable());

        if (menuItem.getCategory() != null) {
            response.setCategory(menuItem.getCategory().getName());
        }

        return response;
    }

    // Convert Category Entity -> CategoryResponse DTO
    public CategoryResponse toCategoryResponse(Category category) {

        CategoryResponse response = new CategoryResponse();

        response.setId(category.getId());
        response.setName(category.getName());

        return response;
    }

    // Convert List<MenuItem> -> List<FoodItemResponse>
    public List<FoodItemResponse> toFoodItemResponseList(List<MenuItem> menuItems) {

        return menuItems.stream()
                .map(this::toFoodItemResponse)
                .collect(Collectors.toList());
    }

    // Convert List<Category> -> List<CategoryResponse>
    public List<CategoryResponse> toCategoryResponseList(List<Category> categories) {

        return categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    // Build MenuResponse
    public MenuResponse toMenuResponse(String category,
                                       List<FoodItemResponse> foodItems) {

        MenuResponse response = new MenuResponse();

        response.setSelectedCategory(category);
        response.setTotalItems(foodItems.size());
        response.setFoodItems(foodItems);

        return response;
    }
}
