package com.backend.mybungalow.attraction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {
    List<Attraction> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<Attraction> findAllByOrderByDisplayOrderAsc();
}

