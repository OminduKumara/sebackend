package com.backend.mybungalow.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private LocalDateTime issuedAt;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String remarks;
    private List<InvoiceItemResponse> items;
}




