package com.scanCrunch.domain.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scanCrunch.domain.cart.entity.Cart;
import com.scanCrunch.domain.menu.entity.MenuItem;
import com.scanCrunch.domain.user.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserAndFoodItem(User user, MenuItem foodItem);

    List<Cart> findByUser(User user);

    void deleteByUser(User user);
}