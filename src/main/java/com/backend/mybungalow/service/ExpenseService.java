package com.backend.mybungalow.service;

import com.backend.mybungalow.dto.ExpenseCreateRequest;
import com.backend.mybungalow.dto.ExpenseResponse;
import com.backend.mybungalow.dto.ExpenseUpdateRequest;

import java.util.List;

public interface ExpenseService {
    ExpenseResponse createExpense(ExpenseCreateRequest request);
    ExpenseResponse updateExpense(Long id, ExpenseUpdateRequest request);
    ExpenseResponse getExpenseById(Long id);
    List<ExpenseResponse> getAllExpenses();
    List<ExpenseResponse> getExpensesByYearAndMonth(int year, int month);
    List<ExpenseResponse> getExpensesByDateRange(String startDate, String endDate);
    void deleteExpense(Long id);
}
