package com.backend.mybungalow.customer.dto;

import com.backend.mybungalow.customer.Customer;

public record CustomerAuthResponse(
    String token,
    String type,
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone
) {
    public static CustomerAuthResponse of(String token, Customer customer) {
        return new CustomerAuthResponse(
            token,
            "Bearer",
            customer.getId(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getEmail(),
            customer.getPhone()
        );
    }
}
