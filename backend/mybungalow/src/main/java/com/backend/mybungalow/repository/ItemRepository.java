package com.backend.mybungalow.repository;

import com.backend.mybungalow.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    List<Item> findByCategoryId(Long categoryId);
}


