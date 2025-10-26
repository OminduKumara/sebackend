package com.backend.mybungalow.reservation.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationCreateRequest(
        @NotBlank String bungalowName,
        @NotNull @Future LocalDate checkInDate,
        @NotNull @Future LocalDate checkOutDate,
        @NotNull Long customerId
) {}


