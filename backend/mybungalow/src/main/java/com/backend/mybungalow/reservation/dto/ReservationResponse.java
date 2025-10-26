package com.backend.mybungalow.reservation.dto;

import java.time.LocalDate;

public record ReservationResponse(
        Long id,
        String bungalowName,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String status
) {}

 