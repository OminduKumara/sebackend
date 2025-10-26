package com.backend.mybungalow.service;

import com.backend.mybungalow.dto.MonthlySummaryResponse;

import java.util.List;

public interface FinanceService {
    MonthlySummaryResponse getMonthlySummary(int year, int month);
    List<MonthlySummaryResponse> getYearlySummary(int year);
    List<MonthlySummaryResponse> getSummaryByDateRange(String startDate, String endDate);
}
