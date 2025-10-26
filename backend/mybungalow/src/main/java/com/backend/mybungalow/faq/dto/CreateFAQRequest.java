package com.backend.mybungalow.faq.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFAQRequest(
        @NotBlank String question,
        @NotBlank String answer,
        String category,
        @NotNull Boolean isActive,
        @NotNull @Min(0) Integer displayOrder
) {}

 