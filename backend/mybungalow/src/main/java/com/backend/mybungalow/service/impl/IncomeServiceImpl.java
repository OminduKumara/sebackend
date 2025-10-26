package com.backend.mybungalow.service.impl;

import com.backend.mybungalow.dto.IncomeCreateRequest;
import com.backend.mybungalow.dto.IncomeResponse;
import com.backend.mybungalow.dto.IncomeUpdateRequest;
import com.backend.mybungalow.exception.ResourceNotFoundException;
import com.backend.mybungalow.model.Income;
import com.backend.mybungalow.repository.IncomeRepository;
import com.backend.mybungalow.service.IncomeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeServiceImpl(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    @Override
    @Transactional
    public IncomeResponse createIncome(IncomeCreateRequest request) {
        Income income = Income.builder()
                .amount(request.getAmount())
                .reason(request.getReason())
                .incomeDate(request.getIncomeDate())
                .remarks(request.getRemarks())
                .category(request.getCategory())
                .build();

        Income saved = incomeRepository.save(income);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public IncomeResponse updateIncome(Long id, IncomeUpdateRequest request) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));

        if (request.getAmount() != null) income.setAmount(request.getAmount());
        if (request.getReason() != null) income.setReason(request.getReason());
        if (request.getIncomeDate() != null) income.setIncomeDate(request.getIncomeDate());
        if (request.getRemarks() != null) income.setRemarks(request.getRemarks());
        if (request.getCategory() != null) income.setCategory(request.getCategory());

        Income updated = incomeRepository.save(income);
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public IncomeResponse getIncomeById(Long id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found with id: " + id));
        return toResponse(income);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getAllIncomes() {
        return incomeRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getIncomesByYearAndMonth(int year, int month) {
        return incomeRepository.findByYearAndMonth(year, month).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<IncomeResponse> getIncomesByDateRange(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return incomeRepository.findByDateRange(start, end).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteIncome(Long id) {
        if (!incomeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Income not found with id: " + id);
        }
        incomeRepository.deleteById(id);
    }

    private IncomeResponse toResponse(Income income) {
        return IncomeResponse.builder()
                .id(income.getId())
                .amount(income.getAmount())
                .reason(income.getReason())
                .incomeDate(income.getIncomeDate())
                .remarks(income.getRemarks())
                .category(income.getCategory())
                .build();
    }
}
