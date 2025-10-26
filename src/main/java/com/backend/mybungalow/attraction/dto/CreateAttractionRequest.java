package com.backend.mybungalow.attraction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAttractionRequest(
        @NotBlank String name,
        @NotBlank String description,
        String category,
        String location,
        String distance,
        @NotNull Boolean isActive,
        @NotNull @Min(0) Integer displayOrder
) {}

