package com.scanCrunch.domain.menu.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scanCrunch.domain.menu.dto.ApiResponse;
import com.scanCrunch.domain.menu.dto.HomeResponse;
import com.scanCrunch.domain.menu.service.HomeService;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping
    public ResponseEntity<ApiResponse<HomeResponse>> getHomePage() {

        HomeResponse response = homeService.getHomeData();

        ApiResponse<HomeResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        "Home data loaded successfully",
                        response
                );

        return ResponseEntity.ok(apiResponse);
    }
}

