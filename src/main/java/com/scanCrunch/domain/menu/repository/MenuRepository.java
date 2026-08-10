package com.scanCrunch.domain.menu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scanCrunch.domain.menu.entity.MenuItem;

@Repository
public interface MenuRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByCategoryNameAndAvailableTrue(String categoryName);

    List<MenuItem> findByNameContainingIgnoreCase(String keyword);

    List<MenuItem> findTop10ByAvailableTrue();

    List<MenuItem> findByAvailableTrue();
}

