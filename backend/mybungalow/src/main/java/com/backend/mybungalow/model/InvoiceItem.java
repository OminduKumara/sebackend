package com.backend.mybungalow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String description;

    @Column(nullable = false)
    @Min(1)
    private Integer quantity;

    @Column(precision = 10, scale = 2, nullable = false)
    @NotNull
    private BigDecimal unitPrice;

    @Column(precision = 10, scale = 2, nullable = false)
    @NotNull
    private BigDecimal lineTotal;
}



