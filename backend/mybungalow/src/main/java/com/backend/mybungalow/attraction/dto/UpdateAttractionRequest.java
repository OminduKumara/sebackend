package com.backend.mybungalow.attraction.dto;

import jakarta.validation.constraints.Min;

public record UpdateAttractionRequest(
        String name,
        String description,
        String category,
        String location,
        String distance,
        Boolean isActive,
        @Min(0) Integer displayOrder
) {}

