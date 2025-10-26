package com.backend.mybungalow.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeUpdateRequest {
    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @NotBlank
    @Size(max = 20)
    private String nic;

    @NotBlank
    @Size(max = 255)
    private String address;

    @Size(max = 50)
    private String phone;

    @NotBlank
    @Size(max = 100)
    private String department;

    @NotBlank
    @Size(max = 100)
    private String position;

    @NotNull
    private LocalDate hireDate;

    @Size(max = 50)
    private String role;

    @Size(max = 20)
    private String status;

    private LocalDate joinDate;

    private LocalDate dateOfBirth;

    @DecimalMin("0.0")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal salary;
}


