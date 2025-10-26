package com.backend.mybungalow.repository;

import com.backend.mybungalow.model.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {
}
