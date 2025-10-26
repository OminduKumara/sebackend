package com.backend.mybungalow.controller;

import com.backend.mybungalow.dto.InvoiceResponse;
import com.backend.mybungalow.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/invoices")
public class AdminInvoiceController {

    private final InvoiceService invoiceService;
    private static final Logger log = LoggerFactory.getLogger(AdminInvoiceController.class);

    public AdminInvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "issuedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<InvoiceResponse> invoices;
            if (customerName != null || customerEmail != null || startDate != null || endDate != null) {
                invoices = invoiceService.getInvoicesWithFilters(customerName, customerEmail, startDate, endDate, pageable);
            } else {
                invoices = invoiceService.getAllInvoices(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("invoices", invoices.getContent());
            response.put("currentPage", invoices.getNumber());
            response.put("totalItems", invoices.getTotalElements());
            response.put("totalPages", invoices.getTotalPages());
            response.put("hasNext", invoices.hasNext());
            response.put("hasPrevious", invoices.hasPrevious());
            
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("Failed to retrieve invoices", ex);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve invoices"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        try {
            InvoiceResponse invoice = invoiceService.getInvoice(id);
            return ResponseEntity.ok(invoice);
        } catch (Exception ex) {
            log.error("Failed to retrieve invoice {}", id, ex);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadInvoicePdf(@PathVariable Long id) {
        try {
            byte[] pdf = invoiceService.renderInvoicePdf(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception ex) {
            log.error("Failed to generate PDF for invoice {}", id, ex);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getInvoiceStats() {
        try {
            // Get basic stats - total invoices, total revenue, etc.
            Page<InvoiceResponse> allInvoices = invoiceService.getAllInvoices(PageRequest.of(0, Integer.MAX_VALUE));
            
            double totalRevenue = allInvoices.getContent().stream()
                    .mapToDouble(invoice -> invoice.getTotal().doubleValue())
                    .sum();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalInvoices", allInvoices.getTotalElements());
            stats.put("totalRevenue", totalRevenue);
            stats.put("averageInvoiceValue", allInvoices.getTotalElements() > 0 ? 
                totalRevenue / allInvoices.getTotalElements() : 0);
            
            return ResponseEntity.ok(stats);
        } catch (Exception ex) {
            log.error("Failed to retrieve invoice stats", ex);
            return ResponseEntity.status(500).body(Map.of("error", "Failed to retrieve invoice statistics"));
        }
    }
}


