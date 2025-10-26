package com.backend.mybungalow.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class IncomeResponse {

    private Long id;
    private BigDecimal amount;
    private String reason;
    private LocalDate incomeDate;
    private String remarks;
    private String category;
}
