package com.backend.mybungalow.faq;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByIsActiveTrueOrderByDisplayOrderAsc();
    List<FAQ> findAllByOrderByDisplayOrderAsc();
}

 