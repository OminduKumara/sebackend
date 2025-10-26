package com.backend.mybungalow.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class MonthlySummaryResponse {

    private int year;
    private int month;
    private String monthName;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal profit;
    private BigDecimal profitPercentage;
    private List<IncomeResponse> incomes;
    private List<ExpenseResponse> expenses;
    private LocalDate startDate;
    private LocalDate endDate;
}
