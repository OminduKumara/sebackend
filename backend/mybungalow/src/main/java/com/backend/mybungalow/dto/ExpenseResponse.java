package com.backend.mybungalow.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class ExpenseResponse {

    private Long id;
    private BigDecimal amount;
    private String reason;
    private LocalDate expenseDate;
    private String remarks;
    private String category;
}
