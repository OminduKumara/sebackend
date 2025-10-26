package com.backend.mybungalow.service.impl;

import com.backend.mybungalow.dto.ExpenseCreateRequest;
import com.backend.mybungalow.dto.ExpenseResponse;
import com.backend.mybungalow.dto.ExpenseUpdateRequest;
import com.backend.mybungalow.exception.ResourceNotFoundException;
import com.backend.mybungalow.model.Expense;
import com.backend.mybungalow.repository.ExpenseRepository;
import com.backend.mybungalow.service.ExpenseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    @Transactional
    public ExpenseResponse createExpense(ExpenseCreateRequest request) {
        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .reason(request.getReason())
                .expenseDate(request.getExpenseDate())
                .remarks(request.getRemarks())
                .category(request.getCategory())
                .build();

        Expense saved = expenseRepository.save(expense);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseUpdateRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (request.getAmount() != null) expense.setAmount(request.getAmount());
        if (request.getReason() != null) expense.setReason(request.getReason());
        if (request.getExpenseDate() != null) expense.setExpenseDate(request.getExpenseDate());
        if (request.getRemarks() != null) expense.setRemarks(request.getRemarks());
        if (request.getCategory() != null) expense.setCategory(request.getCategory());

        Expense updated = expenseRepository.save(expense);
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        return toResponse(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByYearAndMonth(int year, int month) {
        return expenseRepository.findByYearAndMonth(year, month).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByDateRange(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return expenseRepository.findByDateRange(start, end).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteExpense(Long id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }

    private ExpenseResponse toResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .reason(expense.getReason())
                .expenseDate(expense.getExpenseDate())
                .remarks(expense.getRemarks())
                .category(expense.getCategory())
                .build();
    }
}
