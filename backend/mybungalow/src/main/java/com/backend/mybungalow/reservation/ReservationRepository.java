package com.backend.mybungalow.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerIdOrderByCheckInDateDesc(Long customerId);
    List<Reservation> findByCustomerIdAndCheckOutDateBeforeOrderByCheckInDateDesc(Long customerId, LocalDate date);
    List<Reservation> findByCustomerIdAndCheckInDateGreaterThanEqualOrderByCheckInDateAsc(Long customerId, LocalDate date);
}

 