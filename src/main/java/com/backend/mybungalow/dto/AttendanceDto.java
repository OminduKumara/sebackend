package com.backend.mybungalow.dto;

import com.backend.mybungalow.domain.AttendanceStatus;
import java.time.LocalDate;

public record AttendanceDto(
    Long id,
    Long employeeId,
    LocalDate workDate,
    AttendanceStatus status
) {}
