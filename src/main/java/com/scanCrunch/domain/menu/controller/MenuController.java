package com.scanCrunch.domain.menu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.domain.menu.dto.CategoryResponse;
import com.scanCrunch.domain.menu.dto.FoodItemResponse;
import com.scanCrunch.domain.menu.dto.MenuResponse;
import com.scanCrunch.domain.menu.dto.SearchResponse;
import com.scanCrunch.domain.menu.service.MenuService;

@RestController
@RequestMapping("/api/v1/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * GET /api/v1/menu/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> categories = menuService.getAllCategories();

        return ResponseEntity.ok(categories);
    }

    /**
     * GET /api/v1/menu/items
     */
    @GetMapping("/items")
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {

        List<FoodItemResponse> foodItems = menuService.getAllFoodItems();

        return ResponseEntity.ok(foodItems);
    }

    /**
     * GET /api/v1/menu?category=Veg
     */
    @GetMapping
    public ResponseEntity<List<FoodItemResponse>> getMenuByCategory(
            @RequestParam String category) {

        MenuResponse response = menuService.getFoodItemsByCategory(category);

        return ResponseEntity.ok(response.getFoodItems());
    }

    /**
     * GET /api/v1/menu/search?keyword=paneer
     */
    @GetMapping("/search")
    public ResponseEntity<List<SearchResponse>> searchFoodItems(
            @RequestParam String keyword) {

        List<SearchResponse> response = menuService.searchFoodItems(keyword);

        return ResponseEntity.ok(response);
    }
}