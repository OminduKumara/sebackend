package com.backend.mybungalow.reservation;

import com.backend.mybungalow.reservation.dto.AdminReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reservations")
public class AdminReservationController {
    private final ReservationService reservationService;

    public AdminReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<AdminReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservationsForAdmin());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AdminReservationResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<AdminReservationResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus) {
        return ResponseEntity.ok(reservationService.updatePaymentStatus(id, paymentStatus));
    }
}


