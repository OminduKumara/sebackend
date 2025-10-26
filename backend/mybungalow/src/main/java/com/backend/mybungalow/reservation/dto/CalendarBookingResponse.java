package com.backend.mybungalow.reservation.dto;

import java.time.LocalDate;

public record CalendarBookingResponse(
        Long id,
        String bungalowName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String status,
        String customerName,
        String customerEmail
) {}
