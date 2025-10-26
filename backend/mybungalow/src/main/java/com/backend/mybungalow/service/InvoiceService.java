package com.backend.mybungalow.service;

import com.backend.mybungalow.dto.InvoiceCreateRequest;
import com.backend.mybungalow.dto.InvoiceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceCreateRequest request);
    InvoiceResponse getInvoice(Long id);
    List<InvoiceResponse> getInvoicesForCustomer(Long customerId);
    byte[] renderInvoicePdf(Long id);
    
    // Admin methods
    Page<InvoiceResponse> getAllInvoices(Pageable pageable);
    Page<InvoiceResponse> getInvoicesWithFilters(String customerName, String customerEmail, 
                                                LocalDateTime startDate, LocalDateTime endDate, 
                                                Pageable pageable);
}



