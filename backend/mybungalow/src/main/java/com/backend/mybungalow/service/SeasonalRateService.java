package com.backend.mybungalow.service;

import com.backend.mybungalow.dto.SeasonalRateRequestDto;
import com.backend.mybungalow.dto.SeasonalRateResponseDto;

import java.util.List;

public interface SeasonalRateService {
    SeasonalRateResponseDto createSeasonalRate(SeasonalRateRequestDto dto);
    List<SeasonalRateResponseDto> getRatesByRoom(Long roomId);
    SeasonalRateResponseDto updateSeasonalRate(Long id, SeasonalRateRequestDto dto);
    void deleteSeasonalRate(Long id);

}
