package com.backend.mybungalow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "incomes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Amount must have up to 8 digits and 2 decimal places")
    private BigDecimal amount;

    @Column(name = "reason", nullable = false, length = 255)
    @NotBlank(message = "Reason is required")
    @Size(max = 255, message = "Reason must be less than 255 characters")
    private String reason;

    @Column(name = "income_date", nullable = false)
    @NotNull(message = "Income date is required")
    @PastOrPresent(message = "Income date cannot be in the future")
    private LocalDate incomeDate;

    @Column(name = "remarks", length = 500)
    @Size(max = 500, message = "Remarks must be less than 500 characters")
    private String remarks;

    @Column(name = "category", length = 50)
    @Size(max = 50, message = "Category must be less than 50 characters")
    private String category;
}
