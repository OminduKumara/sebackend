package com.backend.mybungalow.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeasonalRateResponseDto {
    private Long id;
    private String roomName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal discountPercentage;
    private BigDecimal newPrice;
}