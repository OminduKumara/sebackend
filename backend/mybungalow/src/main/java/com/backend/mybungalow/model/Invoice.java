package com.backend.mybungalow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long customerId; // optional link to Customer table by id

    @Column(nullable = false, length = 100)
    @NotBlank
    private String customerName;

    @Column(nullable = false, length = 100)
    @NotBlank
    private String customerEmail;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime issuedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @Column(precision = 10, scale = 2, nullable = false)
    @NotNull
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2, nullable = false)
    @NotNull
    private BigDecimal total;

    @Column(length = 500)
    private String remarks;

    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }
}


