package com.backend.mybungalow.service;

import com.backend.mybungalow.dto.IncomeCreateRequest;
import com.backend.mybungalow.dto.IncomeResponse;
import com.backend.mybungalow.dto.IncomeUpdateRequest;

import java.util.List;

public interface IncomeService {
    IncomeResponse createIncome(IncomeCreateRequest request);
    IncomeResponse updateIncome(Long id, IncomeUpdateRequest request);
    IncomeResponse getIncomeById(Long id);
    List<IncomeResponse> getAllIncomes();
    List<IncomeResponse> getIncomesByYearAndMonth(int year, int month);
    List<IncomeResponse> getIncomesByDateRange(String startDate, String endDate);
    void deleteIncome(Long id);
}
