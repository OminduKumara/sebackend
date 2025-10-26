package com.backend.mybungalow.controller;

import com.backend.mybungalow.domain.Attendance;
import com.backend.mybungalow.domain.AttendanceStatus;
import com.backend.mybungalow.service.AttendanceService;
import com.backend.mybungalow.dto.AttendanceDto;
import com.backend.mybungalow.dto.EmployeeAttendanceDetailDto;
import com.backend.mybungalow.dto.EmployeeAttendanceSummaryDto;
import com.backend.mybungalow.dto.WorkingDaysDto;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceDto markAttendance(@Valid @RequestBody AttendanceDto dto) {
        Attendance saved = attendanceService.markAttendance(
                dto.employeeId(), 
                dto.workDate(), 
                dto.status()
        );
        return toDto(saved);
    }

    @PutMapping("/{employeeId}/{workDate}")
    public AttendanceDto updateAttendance(
            @PathVariable Long employeeId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate,
            @RequestParam AttendanceStatus status) {
        Attendance updated = attendanceService.updateAttendance(employeeId, workDate, status);
        return toDto(updated);
    }

    @GetMapping
    public List<AttendanceDto> getAllAttendance() {
        return attendanceService.getAllAttendance().stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/employee/{employeeId}")
    public List<AttendanceDto> getAttendanceByEmployee(@PathVariable Long employeeId) {
        return attendanceService.getAttendanceByEmployee(employeeId).stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/employee/{employeeId}/summary")
    public EmployeeAttendanceSummaryDto getEmployeeAttendanceSummary(@PathVariable Long employeeId) {
        List<Attendance> attendanceRecords = attendanceService.getAttendanceByEmployee(employeeId);
        
        long presentDays = attendanceRecords.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();
        
        long absentDays = attendanceRecords.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.ABSENT)
                .count();
        
        long leaveDays = attendanceRecords.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.LEAVE)
                .count();
        
        return new EmployeeAttendanceSummaryDto(
                employeeId,
                (long) attendanceRecords.size(),
                presentDays,
                absentDays,
                leaveDays
        );
    }

    @GetMapping("/date/{workDate}")
    public List<AttendanceDto> getAttendanceByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate workDate) {
        return attendanceService.getAttendanceByDate(workDate).stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/employee/{employeeId}/range")
    public List<AttendanceDto> getAttendanceByEmployeeAndDateRange(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return attendanceService.getAttendanceByEmployeeAndDateRange(employeeId, startDate, endDate).stream()
                .map(this::toDto)
                .toList();
    }

    @GetMapping("/working-days/{employeeId}")
    public WorkingDaysDto getWorkingDaysForEmployee(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long workingDays = attendanceService.getWorkingDaysForEmployee(employeeId, startDate, endDate);
        return new WorkingDaysDto(employeeId, "Employee " + employeeId, workingDays);
    }

    @GetMapping("/employee/{employeeId}/detailed")
    public EmployeeAttendanceDetailDto getEmployeeAttendanceDetail(@PathVariable Long employeeId) {
        List<Attendance> attendanceRecords = attendanceService.getAttendanceByEmployee(employeeId);
        
        // Get employee details (you might want to inject EmployeeService for this)
        // For now, we'll use a simple approach
        return new EmployeeAttendanceDetailDto(
                employeeId,
                attendanceRecords.stream()
                        .map(this::toDto)
                        .toList()
        );
    }

    private AttendanceDto toDto(Attendance attendance) {
        return new AttendanceDto(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getWorkDate(),
                attendance.getStatus()
        );
    }
}