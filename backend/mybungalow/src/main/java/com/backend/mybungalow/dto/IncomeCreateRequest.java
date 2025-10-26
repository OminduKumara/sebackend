package com.backend.mybungalow.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class IncomeCreateRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount must have up to 8 digits and 2 decimal places")
    private BigDecimal amount;

    @NotBlank(message = "Reason is required")
    @Size(max = 255, message = "Reason must be less than 255 characters")
    private String reason;

    @NotNull(message = "Income date is required")
    @PastOrPresent(message = "Income date cannot be in the future")
    private LocalDate incomeDate;

    @Size(max = 500, message = "Remarks must be less than 500 characters")
    private String remarks;

    @Size(max = 50, message = "Category must be less than 50 characters")
    private String category;
}
