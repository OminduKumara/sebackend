package com.backend.mybungalow.bungalow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BungalowHistoryRepository extends JpaRepository<BungalowHistory, Long> {
    List<BungalowHistory> findAllByOrderByYearDesc();
    List<BungalowHistory> findByCategoryOrderByYearDesc(String category);
}
