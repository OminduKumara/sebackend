package com.backend.mybungalow.reservation;

import com.backend.mybungalow.customer.Customer;
import com.backend.mybungalow.customer.CustomerRepository;
import com.backend.mybungalow.reservation.dto.AdminReservationResponse;
import com.backend.mybungalow.reservation.dto.ReservationCreateRequest;
import com.backend.mybungalow.reservation.dto.ReservationResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;

    public ReservationService(ReservationRepository reservationRepository, CustomerRepository customerRepository) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
    }

    public ReservationResponse createReservation(ReservationCreateRequest request) {
        Customer customer = null;
        if (request.customerId() != null) {
            customer = customerRepository.findById(request.customerId()).orElse(null);
        }
        if (customer == null && request.customerEmail() != null) {
            customer = customerRepository.findByEmail(request.customerEmail())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
        }
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        Reservation reservation = new Reservation();
        reservation.setCustomerId(customer.getId());
        reservation.setBungalowName(request.bungalowName());
        reservation.setCheckInDate(request.checkInDate());
        reservation.setCheckOutDate(request.checkOutDate());
        reservation.setStatus("CONFIRMED");
        reservation.setPaymentStatus("PENDING");

        Reservation saved = reservationRepository.save(reservation);
        return new ReservationResponse(
                saved.getId(),
                saved.getBungalowName(),
                saved.getCheckInDate(),
                saved.getCheckOutDate(),
                saved.getStatus()
        );
    }

    public List<ReservationResponse> getReservationsForCustomer(String email, String filter) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        LocalDate today = LocalDate.now();
        List<Reservation> reservations;
        if ("past".equalsIgnoreCase(filter)) {
            reservations = reservationRepository.findByCustomerIdAndCheckOutDateBeforeOrderByCheckInDateDesc(customer.getId(), today);
        } else if ("upcoming".equalsIgnoreCase(filter)) {
            reservations = reservationRepository.findByCustomerIdAndCheckInDateGreaterThanEqualOrderByCheckInDateAsc(customer.getId(), today);
        } else {
            reservations = reservationRepository.findByCustomerIdOrderByCheckInDateDesc(customer.getId());
        }

        return reservations.stream()
                .map(r -> new ReservationResponse(
                        r.getId(),
                        r.getBungalowName(),
                        r.getCheckInDate(),
                        r.getCheckOutDate(),
                        r.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public List<AdminReservationResponse> getAllReservationsForAdmin() {
        return reservationRepository.findAll().stream()
                .map(r -> {
                    Customer c = customerRepository.findById(r.getCustomerId())
                            .orElse(null);
                    String customerEmail = c != null ? c.getEmail() : null;
                    String customerName = c != null ? (c.getFirstName() + " " + c.getLastName()) : null;
                    return new AdminReservationResponse(
                            r.getId(),
                            r.getBungalowName(),
                            r.getCheckInDate(),
                            r.getCheckOutDate(),
                            r.getStatus(),
                            r.getCustomerId(),
                            customerEmail,
                            customerName,
                            r.getPaymentStatus()
                    );
                })
                .collect(Collectors.toList());
    }

    public AdminReservationResponse updateReservationStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        reservation.setStatus(status);
        Reservation updated = reservationRepository.save(reservation);
        
        Customer c = customerRepository.findById(updated.getCustomerId())
                .orElse(null);
        String customerEmail = c != null ? c.getEmail() : null;
        String customerName = c != null ? (c.getFirstName() + " " + c.getLastName()) : null;
        
        return new AdminReservationResponse(
                updated.getId(),
                updated.getBungalowName(),
                updated.getCheckInDate(),
                updated.getCheckOutDate(),
                updated.getStatus(),
                updated.getCustomerId(),
                customerEmail,
                customerName,
                updated.getPaymentStatus()
        );
    }

    public AdminReservationResponse updatePaymentStatus(Long id, String paymentStatus) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        reservation.setPaymentStatus(paymentStatus);
        Reservation updated = reservationRepository.save(reservation);
        
        Customer c = customerRepository.findById(updated.getCustomerId())
                .orElse(null);
        String customerEmail = c != null ? c.getEmail() : null;
        String customerName = c != null ? (c.getFirstName() + " " + c.getLastName()) : null;
        
        return new AdminReservationResponse(
                updated.getId(),
                updated.getBungalowName(),
                updated.getCheckInDate(),
                updated.getCheckOutDate(),
                updated.getStatus(),
                updated.getCustomerId(),
                customerEmail,
                customerName,
                updated.getPaymentStatus()
        );
    }
}

 