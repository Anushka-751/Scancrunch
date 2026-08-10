package com.scanCrunch.domain.menu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scanCrunch.domain.menu.dto.HomeResponse;
import com.scanCrunch.domain.menu.entity.Category;
import com.scanCrunch.domain.menu.entity.MenuItem;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.domain.menu.mapper.HomeMapper;
import com.scanCrunch.domain.menu.repository.CategoryRepository;
import com.scanCrunch.domain.menu.repository.MenuRepository;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Override
    public HomeResponse getHomeData() {

        List<Category> categories = categoryRepository.findAll();

        List<MenuItem> featuredFoods = menuRepository.findTop10ByAvailableTrue();
        if (featuredFoods == null || featuredFoods.isEmpty()) {
            throw new ResourceNotFoundException("No featured food items found");
        }

        return HomeMapper.mapHomeResponse(
                "ScanCrunch Restaurant",
                "Welcome to ScanCrunch",
                categories,
                featuredFoods);
    }
}

