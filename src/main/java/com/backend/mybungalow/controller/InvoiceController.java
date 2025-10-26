package com.backend.mybungalow.controller;

import com.backend.mybungalow.dto.InvoiceCreateRequest;
import com.backend.mybungalow.dto.InvoiceResponse;
import com.backend.mybungalow.service.InvoiceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody InvoiceCreateRequest request) {
        try {
            return ResponseEntity.ok(invoiceService.createInvoice(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            log.error("Failed to create invoice", ex);
            return ResponseEntity.status(500).body("Failed to create invoice");
        }
    }

    @GetMapping("/{id}")
    public InvoiceResponse get(@PathVariable Long id) {
        return invoiceService.getInvoice(id);
    }

    @GetMapping("/customer/{customerId}")
    public List<InvoiceResponse> listByCustomer(@PathVariable Long customerId) {
        return invoiceService.getInvoicesForCustomer(customerId);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.renderInvoicePdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}


