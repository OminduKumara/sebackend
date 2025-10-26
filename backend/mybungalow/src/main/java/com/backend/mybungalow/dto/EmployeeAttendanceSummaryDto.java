package com.backend.mybungalow.dto;

public record EmployeeAttendanceSummaryDto(
    Long employeeId,
    Long totalDays,
    Long presentDays,
    Long absentDays,
    Long leaveDays
) {}
