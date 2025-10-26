package com.backend.mybungalow.attraction.dto;

import java.time.LocalDateTime;

public record AttractionResponse(
        Long id,
        String name,
        String description,
        String category,
        String location,
        String distance,
        Boolean isActive,
        Integer displayOrder,
        LocalDateTime createdAt
) {}

