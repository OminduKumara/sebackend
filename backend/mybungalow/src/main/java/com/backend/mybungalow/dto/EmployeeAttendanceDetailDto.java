package com.backend.mybungalow.dto;

import java.util.List;

public record EmployeeAttendanceDetailDto(
    Long employeeId,
    List<AttendanceDto> attendanceRecords
) {}
