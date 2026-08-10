package com.scanCrunch.domain.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanCrunch.core.exception.BadRequestException;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.domain.menu.dto.CategoryResponse;
import com.scanCrunch.domain.menu.dto.FoodItemResponse;
import com.scanCrunch.domain.menu.dto.MenuResponse;
import com.scanCrunch.domain.menu.dto.SearchResponse;
import com.scanCrunch.domain.menu.entity.Category;
import com.scanCrunch.domain.menu.entity.MenuItem;
import com.scanCrunch.domain.menu.mapper.MenuMapper;
import com.scanCrunch.domain.menu.repository.CategoryRepository;
import com.scanCrunch.domain.menu.repository.MenuRepository;

@Service
public class MenuServiceImpl implements MenuService {

        private final MenuRepository menuRepository;

        private final CategoryRepository categoryRepository;

        private final MenuMapper menuMapper;

        public MenuServiceImpl(MenuRepository menuRepository,
                        CategoryRepository categoryRepository,
                        MenuMapper menuMapper) {

                this.menuRepository = menuRepository;
                this.categoryRepository = categoryRepository;
                this.menuMapper = menuMapper;
        }

        // Get all categories
        @Override
        public List<CategoryResponse> getAllCategories() {

                List<Category> categories = categoryRepository.findAll();

                return menuMapper.toCategoryResponseList(categories);
        }

        // Get food items by category
        @Override
        public MenuResponse getFoodItemsByCategory(String category) {

                if (category == null || category.trim().isEmpty()) {
                        throw new BadRequestException("Invalid category");
                }

                categoryRepository.findByNameIgnoreCase(category)
                                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

                List<MenuItem> menuItems = menuRepository.findByCategoryNameAndAvailableTrue(category);

                List<FoodItemResponse> foodItems = menuMapper.toFoodItemResponseList(menuItems);

                return menuMapper.toMenuResponse(category, foodItems);
        }

        // Search food items
        @Override
        public List<SearchResponse> searchFoodItems(String keyword) {

                if (keyword == null || keyword.trim().isEmpty()) {
                        throw new BadRequestException("Search keyword cannot be empty");
                }

                List<MenuItem> menuItems = menuRepository.findByNameContainingIgnoreCase(keyword);

                return menuItems.stream()

                                .filter(MenuItem::isAvailable)

                                .map(item -> new SearchResponse(
                                                item.getId(),
                                                item.getName(),
                                                item.getPrice()))

                                .toList();

        }

        // Get all available food items
        @Override
        public List<FoodItemResponse> getAllFoodItems() {

        List<MenuItem> menuItems = menuRepository.findByAvailableTrue();

        return menuMapper.toFoodItemResponseList(menuItems);
        }

}
