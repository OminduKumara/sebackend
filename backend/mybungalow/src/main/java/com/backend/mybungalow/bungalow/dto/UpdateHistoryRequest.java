package com.backend.mybungalow.bungalow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateHistoryRequest(
        @NotBlank(message = "Title is required")
        String title,
        
        @NotBlank(message = "Content is required")
        String content,
        
        @NotBlank(message = "Category is required")
        @Pattern(regexp = "^(CONSTRUCTION|RENOVATION|EVENT|MILESTONE)$", message = "Invalid category")
        String category,
        
        @NotNull(message = "Year is required")
        Integer year,
        
        String imageUrl
) {}
