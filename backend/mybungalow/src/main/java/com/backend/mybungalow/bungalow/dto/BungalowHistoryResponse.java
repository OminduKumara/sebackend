package com.backend.mybungalow.bungalow.dto;

import java.time.LocalDateTime;

public record BungalowHistoryResponse(
        Long id,
        String title,
        String content,
        String category,
        Integer year,
        String imageUrl,
        LocalDateTime createdAt
) {}
