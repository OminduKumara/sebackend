package com.backend.mybungalow.service.impl;

import com.backend.mybungalow.dto.IncomeResponse;
import com.backend.mybungalow.dto.ExpenseResponse;
import com.backend.mybungalow.dto.MonthlySummaryResponse;
import com.backend.mybungalow.repository.IncomeRepository;
import com.backend.mybungalow.repository.ExpenseRepository;
import com.backend.mybungalow.service.FinanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class FinanceServiceImpl implements FinanceService {

    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;

    public FinanceServiceImpl(IncomeRepository incomeRepository, ExpenseRepository expenseRepository) {
        this.incomeRepository = incomeRepository;
        this.expenseRepository = expenseRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public MonthlySummaryResponse getMonthlySummary(int year, int month) {
        // Get total amounts
        BigDecimal totalIncome = incomeRepository.getTotalIncomeByYearAndMonth(year, month);
        BigDecimal totalExpense = expenseRepository.getTotalExpenseByYearAndMonth(year, month);
        
        // Calculate profit
        BigDecimal profit = totalIncome.subtract(totalExpense);
        
        // Calculate profit percentage
        BigDecimal profitPercentage = BigDecimal.ZERO;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            profitPercentage = profit.divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        
        // Get detailed lists
        List<IncomeResponse> incomes = incomeRepository.findByYearAndMonth(year, month).stream()
                .map(income -> IncomeResponse.builder()
                        .id(income.getId())
                        .amount(income.getAmount())
                        .reason(income.getReason())
                        .incomeDate(income.getIncomeDate())
                        .remarks(income.getRemarks())
                        .category(income.getCategory())
                        .build())
                .toList();
                
        List<ExpenseResponse> expenses = expenseRepository.findByYearAndMonth(year, month).stream()
                .map(expense -> ExpenseResponse.builder()
                        .id(expense.getId())
                        .amount(expense.getAmount())
                        .reason(expense.getReason())
                        .expenseDate(expense.getExpenseDate())
                        .remarks(expense.getRemarks())
                        .category(expense.getCategory())
                        .build())
                .toList();
        
        // Calculate date range
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        return MonthlySummaryResponse.builder()
                .year(year)
                .month(month)
                .monthName(Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .profit(profit)
                .profitPercentage(profitPercentage)
                .incomes(incomes)
                .expenses(expenses)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlySummaryResponse> getYearlySummary(int year) {
        List<MonthlySummaryResponse> monthlySummaries = new ArrayList<>();
        
        for (int month = 1; month <= 12; month++) {
            MonthlySummaryResponse summary = getMonthlySummary(year, month);
            monthlySummaries.add(summary);
        }
        
        return monthlySummaries;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlySummaryResponse> getSummaryByDateRange(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        
        // Get total amounts for the date range
        BigDecimal totalIncome = incomeRepository.getTotalIncomeByDateRange(start, end);
        BigDecimal totalExpense = expenseRepository.getTotalExpenseByDateRange(start, end);
        
        // Calculate profit
        BigDecimal profit = totalIncome.subtract(totalExpense);
        
        // Calculate profit percentage
        BigDecimal profitPercentage = BigDecimal.ZERO;
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            profitPercentage = profit.divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
        
        // Get detailed lists
        List<IncomeResponse> incomes = incomeRepository.findByDateRange(start, end).stream()
                .map(income -> IncomeResponse.builder()
                        .id(income.getId())
                        .amount(income.getAmount())
                        .reason(income.getReason())
                        .incomeDate(income.getIncomeDate())
                        .remarks(income.getRemarks())
                        .category(income.getCategory())
                        .build())
                .toList();
                
        List<ExpenseResponse> expenses = expenseRepository.findByDateRange(start, end).stream()
                .map(expense -> ExpenseResponse.builder()
                        .id(expense.getId())
                        .amount(expense.getAmount())
                        .reason(expense.getReason())
                        .expenseDate(expense.getExpenseDate())
                        .remarks(expense.getRemarks())
                        .category(expense.getCategory())
                        .build())
                .toList();
        
        MonthlySummaryResponse summary = MonthlySummaryResponse.builder()
                .year(start.getYear())
                .month(start.getMonthValue())
                .monthName("Custom Range")
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .profit(profit)
                .profitPercentage(profitPercentage)
                .incomes(incomes)
                .expenses(expenses)
                .startDate(start)
                .endDate(end)
                .build();
        
        return List.of(summary);
    }
}
