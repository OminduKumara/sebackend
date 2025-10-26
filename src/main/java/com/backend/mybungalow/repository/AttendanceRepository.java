package com.backend.mybungalow.repository;

import com.backend.mybungalow.domain.Attendance;
import com.backend.mybungalow.domain.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByEmployeeIdAndWorkDate(Long id, LocalDate workDate);

    List<Attendance> findByEmployeeIdAndWorkDateBetween(Long id, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByWorkDate(LocalDate workDate);

    List<Attendance> findByEmployeeId(Long id);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :id " +
            "AND a.workDate BETWEEN :startDate AND :endDate " +
            "AND a.status = :status")
    Long countWorkingDaysByEmployeeAndDateRange(@Param("id") Long id,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate,
                                                @Param("status") AttendanceStatus status);

}
