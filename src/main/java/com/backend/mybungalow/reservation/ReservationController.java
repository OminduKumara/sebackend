package com.backend.mybungalow.reservation;

import com.backend.mybungalow.reservation.dto.ReservationCreateRequest;
import com.backend.mybungalow.reservation.dto.ReservationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    // GET /api/customers/reservations?email=...&filter=past|upcoming|all
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @RequestParam String email,
            @RequestParam(required = false, defaultValue = "all") String filter
    ) {
        return ResponseEntity.ok(reservationService.getReservationsForCustomer(email, filter));
    }

    // POST /api/customers/reservations
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }
}

 