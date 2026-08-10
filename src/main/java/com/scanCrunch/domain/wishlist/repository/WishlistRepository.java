package com.scanCrunch.domain.wishlist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanCrunch.domain.menu.entity.MenuItem;
import com.scanCrunch.domain.user.entity.User;
import com.scanCrunch.domain.wishlist.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUser(User user);

    Optional<Wishlist> findByUserAndMenu(User user, MenuItem menu);

    Optional<Wishlist> findByIdAndUser(Long wishlistId, User user);

    boolean existsByUserAndMenu(User user, MenuItem menu);

    void deleteByUserAndMenu(User user, MenuItem menu);

    void deleteByUser(User user);
}
