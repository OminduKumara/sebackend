package com.backend.mybungalow.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String nic;
    private String address;
    private String phone;
    private String department;
    private String position;
    private LocalDate hireDate;
    private String role;
    private String status;
    private LocalDate joinDate;
    private LocalDate dateOfBirth;
    private BigDecimal salary;
}


