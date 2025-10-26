package com.backend.mybungalow.reservation.dto;

import java.time.LocalDate;

public record AdminReservationResponse(
        Long id,
        String bungalowName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String status,
        Long customerId,
        String customerEmail,
        String customerName
) {}


