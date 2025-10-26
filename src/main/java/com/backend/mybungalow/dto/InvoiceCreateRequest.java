package com.backend.mybungalow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceCreateRequest {
    private Long customerId;
    @NotBlank
    private String customerName;
    @NotBlank
    @Email
    private String customerEmail;
    private String remarks;
    private List<InvoiceItemRequest> items;
}


