package com.backend.mybungalow.service.impl;

import com.backend.mybungalow.dto.SeasonalRateRequestDto;
import com.backend.mybungalow.dto.SeasonalRateResponseDto;
import com.backend.mybungalow.exception.ResourceNotFoundException;
import com.backend.mybungalow.model.Room;
import com.backend.mybungalow.model.SeasonalRate;
import com.backend.mybungalow.repository.RoomRepository;
import com.backend.mybungalow.repository.SeasonalRateRepository;
import com.backend.mybungalow.service.SeasonalRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeasonalRateServiceImpl implements SeasonalRateService {
    private final SeasonalRateRepository seasonalRateRepository;
    private final RoomRepository roomRepository;

    @Override
    public SeasonalRateResponseDto createSeasonalRate(SeasonalRateRequestDto dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID " + dto.getRoomId()));

        BigDecimal discount = dto.getDiscountPercentage()
                .divide(BigDecimal.valueOf(100))
                .multiply(room.getPrice());

        BigDecimal newPrice = room.getPrice().subtract(discount);

        SeasonalRate rate = SeasonalRate.builder()
                .room(room)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .discountPercentage(dto.getDiscountPercentage())
                .newPrice(newPrice)
                .build();

        SeasonalRate saved = seasonalRateRepository.save(rate);

        return SeasonalRateResponseDto.builder()
                .id(saved.getId())
                .roomName(room.getName())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .discountPercentage(saved.getDiscountPercentage())
                .newPrice(saved.getNewPrice())
                .build();
    }


    @Transactional(readOnly = true)
    @Override
    public List<SeasonalRateResponseDto> getRatesByRoom(Long roomId) {
        return seasonalRateRepository.findByRoomId(roomId)
                .stream()
                .map(rate -> SeasonalRateResponseDto.builder()
                        .id(rate.getId())
                        .roomName(rate.getRoom().getName())
                        .startDate(rate.getStartDate())
                        .endDate(rate.getEndDate())
                        .discountPercentage(rate.getDiscountPercentage())
                        .newPrice(rate.getNewPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public SeasonalRateResponseDto updateSeasonalRate(Long id, SeasonalRateRequestDto dto) {
        SeasonalRate rate = seasonalRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seasonal rate not found with id " + id));

        // Update fields
        rate.setStartDate(dto.getStartDate());
        rate.setEndDate(dto.getEndDate());
        rate.setDiscountPercentage(dto.getDiscountPercentage());

        // Recalculate new price
        BigDecimal discount = dto.getDiscountPercentage()
                .divide(BigDecimal.valueOf(100))
                .multiply(rate.getRoom().getPrice());
        rate.setNewPrice(rate.getRoom().getPrice().subtract(discount));

        seasonalRateRepository.save(rate);

        return SeasonalRateResponseDto.builder()
                .id(rate.getId())
                .roomName(rate.getRoom().getName())
                .startDate(rate.getStartDate())
                .endDate(rate.getEndDate())
                .discountPercentage(rate.getDiscountPercentage())
                .newPrice(rate.getNewPrice())
                .build();
    }

    @Override
    public void deleteSeasonalRate(Long id) {
        SeasonalRate rate = seasonalRateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seasonal rate not found with id " + id));
        seasonalRateRepository.delete(rate);
    }

}

