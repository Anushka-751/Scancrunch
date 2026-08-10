package com.scanCrunch.domain.wishlist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.scanCrunch.core.exception.BadRequestException;
import com.scanCrunch.core.exception.DuplicateWishlistException;
import com.scanCrunch.core.exception.ResourceNotFoundException;
import com.scanCrunch.core.security.SecurityUtils;
import com.scanCrunch.domain.menu.entity.MenuItem;
import com.scanCrunch.domain.menu.repository.MenuRepository;
import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.wishlist.dto.WishlistResponse;
import com.scanCrunch.domain.wishlist.entity.Wishlist;
import com.scanCrunch.domain.wishlist.mapper.WishlistMapper;
import com.scanCrunch.domain.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final MenuRepository menuRepository;
    private final WishlistMapper wishlistMapper;
    private final SecurityUtils securityUtils;

    // Add a menu item to the logged-in user's wishlist
    @Override
    public WishlistResponse addToWishlist(Long menuId) {

        User currentUser = getAuthenticatedUser();

        MenuItem menuItem = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        if (!menuItem.isAvailable()) {
            throw new BadRequestException("Menu item is not available");
        }

        if (wishlistRepository.existsByUserAndMenu(currentUser, menuItem)) {
            throw new DuplicateWishlistException("Product already exists in wishlist");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(currentUser);
        wishlist.setMenu(menuItem);

        wishlist = wishlistRepository.save(wishlist);

        return wishlistMapper.toWishlistResponse(wishlist);
    }

    // Get the full wishlist for the logged-in user
    @Override
    public List<WishlistResponse> getWishlist() {

        User currentUser = getAuthenticatedUser();

        List<Wishlist> wishlistItems = wishlistRepository.findByUser(currentUser);

        return wishlistMapper.toWishlistResponseList(wishlistItems);
    }

    // Remove a single item, only if it belongs to the logged-in user
    @Override
    public void removeFromWishlist(Long wishlistId) {

        User currentUser = getAuthenticatedUser();

        Wishlist wishlist = wishlistRepository.findByIdAndUser(wishlistId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));

        wishlistRepository.delete(wishlist);
    }

    // Check whether a menu item is already wishlisted by the logged-in user
    @Override
    public boolean isWishlisted(Long menuId) {

        User currentUser = getAuthenticatedUser();

        MenuItem menuItem = menuRepository.findById(menuId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        return wishlistRepository.existsByUserAndMenu(currentUser, menuItem);
    }

    // Clear the entire wishlist for the logged-in user
    @Override
    public void clearWishlist() {

        User currentUser = getAuthenticatedUser();

        wishlistRepository.deleteByUser(currentUser);
    }

    private User getAuthenticatedUser() {

        User currentUser = securityUtils.getCurrentUser();

        if (currentUser == null) {
            throw new BadRequestException("User not authenticated");
        }

        return currentUser;
    }
}
