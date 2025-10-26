package com.backend.mybungalow.controller;

import com.backend.mybungalow.dto.MonthlySummaryResponse;
import com.backend.mybungalow.service.FinanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "*")
public class FinanceController {

    private final FinanceService financeService;

    public FinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam int year,
            @RequestParam int month) {
        MonthlySummaryResponse response = financeService.getMonthlySummary(year, month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/yearly-summary")
    public ResponseEntity<List<MonthlySummaryResponse>> getYearlySummary(@RequestParam int year) {
        List<MonthlySummaryResponse> responses = financeService.getYearlySummary(year);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/range-summary")
    public ResponseEntity<List<MonthlySummaryResponse>> getSummaryByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<MonthlySummaryResponse> responses = financeService.getSummaryByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }
}
