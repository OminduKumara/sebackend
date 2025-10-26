package com.backend.mybungalow.service;

import com.backend.mybungalow.domain.Attendance;
import com.backend.mybungalow.domain.AttendanceStatus;
import com.backend.mybungalow.model.Employee;
import com.backend.mybungalow.repository.AttendanceRepository;
import com.backend.mybungalow.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             EmployeeRepository employeeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
    }

    public Attendance markAttendance(Long id, LocalDate workDate, AttendanceStatus status) {
        // Check if employee exists
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with ID: " + id));

        // Check if attendance already exists for this employee and date
        if (attendanceRepository.findByEmployeeIdAndWorkDate(id, workDate).isPresent()) {
            throw new BadRequestException("Attendance already marked for employee " + id + " on " + workDate);
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setWorkDate(workDate);
        attendance.setStatus(status);

        return attendanceRepository.save(attendance);
    }

    public Attendance updateAttendance(Long id, LocalDate workDate, AttendanceStatus status) {
        Attendance existing = attendanceRepository.findByEmployeeIdAndWorkDate(id, workDate)
                .orElseThrow(() -> new NotFoundException("Attendance not found for employee " + id + " on " + workDate));

        existing.setStatus(status);
        return attendanceRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByEmployee(Long id) {
        return attendanceRepository.findByEmployeeId(id);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByDate(LocalDate workDate) {
        return attendanceRepository.findByWorkDate(workDate);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByEmployeeAndDateRange(Long id, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndWorkDateBetween(id, startDate, endDate);
    }

    @Transactional(readOnly = true)
    public Long getWorkingDaysForEmployee(Long id, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.countWorkingDaysByEmployeeAndDateRange(id, startDate, endDate, AttendanceStatus.PRESENT);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }
}