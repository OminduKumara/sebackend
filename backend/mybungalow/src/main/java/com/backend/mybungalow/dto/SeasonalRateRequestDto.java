package com.backend.mybungalow.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeasonalRateRequestDto {
    private Long roomId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal discountPercentage;
}