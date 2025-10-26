package com.backend.mybungalow.controller;

import com.backend.mybungalow.dto.IncomeCreateRequest;
import com.backend.mybungalow.dto.IncomeResponse;
import com.backend.mybungalow.dto.IncomeUpdateRequest;
import com.backend.mybungalow.service.IncomeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incomes")
@CrossOrigin(origins = "*")
public class IncomeController {

    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @PostMapping
    public ResponseEntity<IncomeResponse> createIncome(@Valid @RequestBody IncomeCreateRequest request) {
        IncomeResponse response = incomeService.createIncome(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponse> getIncomeById(@PathVariable Long id) {
        IncomeResponse response = incomeService.getIncomeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getAllIncomes() {
        List<IncomeResponse> responses = incomeService.getAllIncomes();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<IncomeResponse>> getIncomesByMonth(
            @RequestParam int year,
            @RequestParam int month) {
        List<IncomeResponse> responses = incomeService.getIncomesByYearAndMonth(year, month);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/range")
    public ResponseEntity<List<IncomeResponse>> getIncomesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<IncomeResponse> responses = incomeService.getIncomesByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeUpdateRequest request) {
        IncomeResponse response = incomeService.updateIncome(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}
