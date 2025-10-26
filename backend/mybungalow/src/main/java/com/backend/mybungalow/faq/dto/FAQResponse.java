package com.backend.mybungalow.faq.dto;

import java.time.LocalDateTime;

public record FAQResponse(
        Long id,
        String question,
        String answer,
        String category,
        Boolean isActive,
        Integer displayOrder,
        LocalDateTime createdAt
) {}

 