package com.backend.mybungalow.controller;

import com.backend.mybungalow.dto.SeasonalRateRequestDto;
import com.backend.mybungalow.dto.SeasonalRateResponseDto;
import com.backend.mybungalow.service.SeasonalRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seasonal_rates")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SeasonalRateController {

    private final SeasonalRateService seasonalRateService;

    @PostMapping
    public ResponseEntity<SeasonalRateResponseDto> create(@RequestBody SeasonalRateRequestDto dto) {
        return ResponseEntity.ok(seasonalRateService.createSeasonalRate(dto));
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<SeasonalRateResponseDto>> getByRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(seasonalRateService.getRatesByRoom(roomId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeasonalRateResponseDto> updateSeasonalRate(
            @PathVariable Long id,
            @RequestBody SeasonalRateRequestDto dto) {

        SeasonalRateResponseDto updated = seasonalRateService.updateSeasonalRate(id, dto);
        return ResponseEntity.ok(updated);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeasonalRate(@PathVariable Long id) {
        seasonalRateService.deleteSeasonalRate(id);
        return ResponseEntity.noContent().build();
    }
}
