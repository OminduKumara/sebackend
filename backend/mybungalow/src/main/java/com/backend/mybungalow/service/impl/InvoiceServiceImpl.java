package com.backend.mybungalow.service.impl;

import com.backend.mybungalow.dto.*;
import com.backend.mybungalow.model.Invoice;
import com.backend.mybungalow.model.InvoiceItem;
import com.backend.mybungalow.repository.InvoiceRepository;
import com.backend.mybungalow.service.InvoiceService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional
    public InvoiceResponse createInvoice(InvoiceCreateRequest request) {
        BigDecimal subtotal = BigDecimal.ZERO;
        Long customerId = request.getCustomerId() != null ? request.getCustomerId() : 0L; // fallback if DB requires non-null
        Invoice invoice = Invoice.builder()
                .customerId(customerId)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .issuedAt(LocalDateTime.now())
                .subtotal(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .remarks(request.getRemarks())
                .build();

        if (request.getItems() != null) {
            for (InvoiceItemRequest itemReq : request.getItems()) {
                if (itemReq == null) continue;
                if (itemReq.getDescription() == null || itemReq.getDescription().isBlank()) continue;
                if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) continue;
                if (itemReq.getUnitPrice() == null) continue;
                
                BigDecimal lineTotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
                
                // Check for maximum total amount (1 million LKR)
                BigDecimal newSubtotal = subtotal.add(lineTotal);
                if (newSubtotal.compareTo(new BigDecimal("1000000")) > 0) {
                    throw new IllegalArgumentException("Invoice total cannot exceed 1,000,000 LKR");
                }
                
                subtotal = newSubtotal;
                InvoiceItem item = InvoiceItem.builder()
                        .description(itemReq.getDescription())
                        .quantity(itemReq.getQuantity())
                        .unitPrice(itemReq.getUnitPrice())
                        .lineTotal(lineTotal)
                        .build();
                invoice.addItem(item);
            }
        }
        invoice.setSubtotal(subtotal);
        invoice.setTotal(subtotal); // no taxes/discounts for now

        Invoice saved = invoiceRepository.save(invoice);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return toResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoicesForCustomer(Long customerId) {
        return invoiceRepository.findByCustomerIdOrderByIssuedAtDesc(customerId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAllInvoices(Pageable pageable) {
        return invoiceRepository.findAllByOrderByIssuedAtDesc(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getInvoicesWithFilters(String customerName, String customerEmail, 
                                                        LocalDateTime startDate, LocalDateTime endDate, 
                                                        Pageable pageable) {
        return invoiceRepository.findInvoicesWithFilters(customerName, customerEmail, startDate, endDate, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] renderInvoicePdf(Long id) {
        try {
            log.info("Starting PDF generation for invoice {}", id);
            InvoiceResponse invoice = getInvoice(id);
            log.info("Retrieved invoice data for ID: {}, customer: {}, items: {}", id, invoice.getCustomerName(), invoice.getItems().size());
            
            // Handle remarks safely - might be null if database doesn't have the column yet
            String remarks = "";
            try {
                if (invoice.getRemarks() != null && !invoice.getRemarks().trim().isEmpty()) {
                    remarks = String.format("<div class='remarks'><strong>Remarks:</strong> %s</div>", escape(invoice.getRemarks()));
                }
            } catch (Exception e) {
                log.warn("Could not access remarks field for invoice {}: {}", id, e.getMessage());
                remarks = "";
            }
            
            // Use a simpler, more reliable template for PDF generation
            String simpleHtml = String.format("""
<!DOCTYPE html>
<html>
<head>
  <meta charset='utf-8'/>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.4; }
    .letterhead {
      background: #B8860B;
      color: white;
      padding: 30px;
      text-align: center;
      margin-bottom: 30px;
    }
    .company-name {
      font-size: 28px;
      font-weight: bold;
      margin-bottom: 10px;
    }
    .company-tagline {
      font-size: 14px;
      margin-bottom: 15px;
      font-style: italic;
    }
    .company-info {
      font-size: 12px;
      line-height: 1.5;
    }
    .invoice-title {
      font-size: 24px;
      font-weight: bold;
      color: #B8860B;
      margin: 20px 0;
      text-align: center;
    }
    .invoice-details {
      background: #FEF9E7;
      padding: 15px;
      border-left: 4px solid #B8860B;
      margin: 20px 0;
    }
    .customer-info {
      background: #ffffff;
      padding: 15px;
      border: 1px solid #B8860B;
      margin: 20px 0;
    }
    .customer-info h3 {
      margin: 0 0 10px 0;
      color: #B8860B;
      font-size: 16px;
    }
    table {
      width: 100%%;
      border-collapse: collapse;
      margin: 20px 0;
    }
    th {
      background: #B8860B;
      color: white;
      padding: 10px;
      text-align: left;
      font-weight: bold;
    }
    td {
      padding: 10px;
      border-bottom: 1px solid #ddd;
    }
    tr:nth-child(even) {
      background: #FEF9E7;
    }
    .total-section {
      background: #FEF9E7;
      padding: 20px;
      margin-top: 20px;
      border: 2px solid #B8860B;
    }
    .total-amount {
      font-size: 18px;
      font-weight: bold;
      color: #B8860B;
      text-align: right;
      margin-top: 10px;
    }
    .signature-section {
      margin-top: 40px;
      text-align: right;
    }
    .signature-line {
      border-bottom: 2px dotted #B8860B;
      width: 200px;
      margin-left: auto;
      margin-top: 20px;
    }
    .signature-label {
      color: #B8860B;
      font-size: 12px;
      margin-top: 5px;
    }
    .remarks {
      margin-top: 20px;
      padding: 10px;
      background: #FEF9E7;
      border-left: 4px solid #B8860B;
    }
    .footer {
      margin-top: 40px;
      padding: 20px;
      background: #FEF9E7;
      text-align: center;
      border-top: 3px solid #B8860B;
    }
    .footer strong {
      color: #B8860B;
    }
  </style>
</head>
<body>
  <div class='letterhead'>
    <div class='company-name'>Galle My Bungalow</div>
    <div class='company-tagline'>Established Since 1938</div>
    <div class='company-info'>
      165, Bandaranayake Place, Galle<br />
      Phone: 0713178806
    </div>
  </div>

  <div class='invoice-title'>INVOICE #%d</div>

  <div class='invoice-details'>
    <p><strong>Invoice Date:</strong> %s</p>
    <p><strong>Status:</strong> Due</p>
  </div>

  <div class='customer-info'>
    <h3>Bill To:</h3>
    <p><strong>%s</strong></p>
    <p>%s</p>
  </div>

  <table>
    <thead>
      <tr>
        <th>Description</th>
        <th>Qty</th>
        <th>Unit Price</th>
        <th>Amount</th>
      </tr>
    </thead>
    <tbody>
      %s
    </tbody>
  </table>

  <div class='total-section'>
    <div><strong>Subtotal:</strong> %s</div>
    <div class='total-amount'>Total: %s</div>
  </div>
  %s
  <div class='signature-section'>
    <div class='signature-line'></div>
    <div class='signature-label'>Place Owner's Signature</div>
  </div>

  <div class='footer'>
    <p><strong>Thank you for choosing Galle My Bungalow!</strong></p>
    <p>We appreciate your business and look forward to serving you again!</p>
  </div>
</body>
</html>
""", 
                invoice.getId(),
                invoice.getIssuedAt() != null ? invoice.getIssuedAt().toLocalDate().toString() : "N/A",
                escape(invoice.getCustomerName()),
                escape(invoice.getCustomerEmail()),
                buildTableRows(invoice),
                currency(invoice.getSubtotal()),
                currency(invoice.getTotal()),
                remarks
            );
            
            log.info("Generated simple HTML template for invoice {}, length: {} characters", id, simpleHtml.length());
            
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(simpleHtml, null);
                builder.toStream(os);
                builder.run();
                
                byte[] pdfBytes = os.toByteArray();
                if (pdfBytes.length == 0) {
                    throw new RuntimeException("Generated PDF is empty");
                }
                log.info("Successfully generated PDF for invoice {}, size: {} bytes", id, pdfBytes.length);
                return pdfBytes;
            }
        } catch (Exception e) {
            log.error("Failed to generate PDF for invoice {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to generate invoice PDF: " + e.getMessage(), e);
        }
    }
    
    private String buildTableRows(InvoiceResponse invoice) {
        StringBuilder rows = new StringBuilder();
        for (InvoiceItemResponse item : invoice.getItems()) {
            rows.append(String.format("<tr><td>%s</td><td>%d</td><td>LKR %.2f</td><td>LKR %.2f</td></tr>",
                escape(item.getDescription()),
                item.getQuantity(),
                item.getUnitPrice().doubleValue(),
                item.getLineTotal().doubleValue()
            ));
        }
        return rows.toString();
    }

    private byte[] generateSimplePdf(InvoiceResponse invoice) {
        try {
            log.info("Generating simple PDF for invoice {}", invoice.getId());
            
            // Handle remarks safely
            String remarks = "";
            try {
                if (invoice.getRemarks() != null && !invoice.getRemarks().trim().isEmpty()) {
                    remarks = String.format("<div class='remarks'><strong>Remarks:</strong> %s</div>", escape(invoice.getRemarks()));
                }
            } catch (Exception e) {
                log.warn("Could not access remarks field for invoice {}: {}", invoice.getId(), e.getMessage());
                remarks = "";
            }
            
            String simpleHtml = String.format("""
<!DOCTYPE html>
<html>
<head>
  <meta charset='utf-8'/>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.4; }
    .header { background: linear-gradient(135deg, #B8860B 0%%, #D4AF37 100%%); color: white; padding: 25px; text-align: center; margin-bottom: 25px; border-radius: 8px; }
    .company { font-size: 26px; font-weight: bold; margin-bottom: 8px; }
    .tagline { font-size: 14px; margin-bottom: 15px; font-style: italic; }
    .company-info { font-size: 12px; line-height: 1.5; }
    .invoice-title { font-size: 22px; font-weight: bold; margin: 25px 0; color: #B8860B; }
    .invoice-details { background: #FEF9E7; padding: 15px; border-left: 4px solid #B8860B; margin: 20px 0; }
    table { width: 100%%; border-collapse: collapse; margin: 25px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
    th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }
    th { background: #B8860B; color: white; font-weight: bold; }
    tr:nth-child(even) { background: #FEF9E7; }
    .total { text-align: right; font-weight: bold; margin-top: 25px; font-size: 16px; color: #B8860B; }
    .signature-section { margin-top: 40px; text-align: right; }
    .signature-line { border-bottom: 2px dotted #B8860B; width: 200px; margin-left: auto; margin-top: 20px; }
    .signature-label { color: #B8860B; font-size: 12px; margin-top: 5px; }
    .remarks { margin-top: 20px; padding: 10px; background: #FEF9E7; border-left: 4px solid #B8860B; }
    .footer { margin-top: 30px; padding: 20px; background: #FEF9E7; text-align: center; border-top: 3px solid #B8860B; }
  </style>
</head>
<body>
  <div class='header'>
    <div class='company'>Galle My Bungalow</div>
    <div class='tagline'>Established Since 1938</div>
    <div class='company-info'>
      165, Bandaranayake Place, Galle<br />
      Phone: 0713178806
    </div>
  </div>
  
  <div class='invoice-title'>INVOICE #%d</div>
  
  <div class='invoice-details'>
    <p><strong>Bill To:</strong> %s (%s)</p>
    <p><strong>Date:</strong> %s</p>
  </div>
  
  <table>
    <tr><th>Description</th><th>Qty</th><th>Unit Price</th><th>Amount</th></tr>
    %s
  </table>
  
  <div class='total'>Total: %s</div>
  %s
  <div class='signature-section'>
    <div class='signature-line'></div>
      <div class='signature-label'>Place Owner's Signature</div>
  </div>
  
  <div class='footer'>
    <p><strong>Thank you for choosing Galle My Bungalow!</strong></p>
    <p>For inquiries: 0713178806 | 165, Bandaranayake Place, Galle</p>
    <p style='font-style: italic; color: #B8860B; margin-top: 10px;'>We appreciate your business!</p>
  </div>
</body>
</html>
""", 
                invoice.getId(),
                escape(invoice.getCustomerName()),
                escape(invoice.getCustomerEmail()),
                invoice.getIssuedAt() != null ? invoice.getIssuedAt().toLocalDate().toString() : "N/A",
                buildSimpleTableRows(invoice),
                currency(invoice.getTotal()),
                remarks
            );
            
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(simpleHtml, null);
                builder.toStream(os);
                builder.run();
                
                byte[] pdfBytes = os.toByteArray();
                if (pdfBytes.length == 0) {
                    throw new RuntimeException("Simple PDF generation produced empty result");
                }
                log.info("Successfully generated simple PDF for invoice {}, size: {} bytes", invoice.getId(), pdfBytes.length);
                return pdfBytes;
            }
        } catch (Exception e) {
            log.error("Simple PDF generation also failed: {}, trying basic fallback", e.getMessage(), e);
            // Last resort: try with minimal HTML
            return generateBasicPdf(invoice);
        }
    }
    
    private String buildSimpleTableRows(InvoiceResponse invoice) {
        StringBuilder rows = new StringBuilder();
        for (InvoiceItemResponse item : invoice.getItems()) {
            rows.append("<tr>")
                .append("<td>").append(escape(item.getDescription())).append("</td>")
                .append("<td>").append(item.getQuantity()).append("</td>")
                .append("<td>").append(currency(item.getUnitPrice())).append("</td>")
                .append("<td>").append(currency(item.getLineTotal())).append("</td>")
                .append("</tr>");
        }
        return rows.toString();
    }

    private byte[] generateBasicPdf(InvoiceResponse invoice) {
        try {
            log.info("Generating basic PDF for invoice {}", invoice.getId());
            
            // Handle remarks safely
            String remarks = "";
            try {
                if (invoice.getRemarks() != null && !invoice.getRemarks().trim().isEmpty()) {
                    remarks = String.format("<div class='remarks'><strong>Remarks:</strong> %s</div>", escape(invoice.getRemarks()));
                }
            } catch (Exception e) {
                log.warn("Could not access remarks field for invoice {}: {}", invoice.getId(), e.getMessage());
                remarks = "";
            }
            
            // Minimal HTML that should work with any PDF renderer
            String basicHtml = String.format("""
<!DOCTYPE html>
<html>
<head>
<meta charset='utf-8'/>
<style>
body { font-family: Arial, sans-serif; margin: 20px; }
h1 { color: #B8860B; text-align: center; margin-bottom: 10px; }
h2 { color: #B8860B; margin: 20px 0; }
p { margin: 10px 0; }
table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
th { background: #B8860B; color: white; }
.signature-section { margin-top: 40px; text-align: right; }
.signature-line { border-bottom: 2px dotted #B8860B; width: 200px; margin-left: auto; margin-top: 20px; }
.signature-label { color: #B8860B; font-size: 12px; margin-top: 5px; }
.remarks { margin-top: 20px; padding: 10px; background: #FEF9E7; border-left: 4px solid #B8860B; }
</style>
</head>
<body>
<h1>Galle My Bungalow</h1>
<p style='text-align: center; color: #666;'>165, Bandaranayake Place, Galle | Phone: 0713178806<br />Established Since 1938</p>
<h2>INVOICE #%d</h2>
<p><strong>Bill To:</strong> %s (%s)</p>
<p><strong>Date:</strong> %s</p>
<table>
<tr><th>Description</th><th>Qty</th><th>Unit Price</th><th>Amount</th></tr>
%s
</table>
<p><strong>Total: %s</strong></p>
%s
<div class='signature-section'>
  <div class='signature-line'></div>
      <div class='signature-label'>Place Owner's Signature</div>
</div>
<p style='text-align: center; margin-top: 30px; color: #666;'>Thank you for choosing Galle My Bungalow!</p>
</body>
</html>
""", 
                invoice.getId(),
                escape(invoice.getCustomerName()),
                escape(invoice.getCustomerEmail()),
                invoice.getIssuedAt() != null ? invoice.getIssuedAt().toLocalDate().toString() : "N/A",
                buildSimpleTableRows(invoice),
                currency(invoice.getTotal()),
                remarks
            );
            
            try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.useFastMode();
                builder.withHtmlContent(basicHtml, null);
                builder.toStream(os);
                builder.run();
                
                byte[] pdfBytes = os.toByteArray();
                if (pdfBytes.length == 0) {
                    throw new RuntimeException("Basic PDF generation produced empty result");
                }
                log.info("Successfully generated basic PDF for invoice {}, size: {} bytes", invoice.getId(), pdfBytes.length);
                return pdfBytes;
            }
        } catch (Exception e) {
            log.error("Basic PDF generation failed: {}", e.getMessage(), e);
            throw new RuntimeException("All PDF generation methods failed", e);
        }
    }

    private String currency(BigDecimal amount) {
        return "LKR " + amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String buildInvoiceHtml(InvoiceResponse invoice) {
        StringBuilder rows = new StringBuilder();
        for (InvoiceItemResponse item : invoice.getItems()) {
            rows.append("<tr>")
                .append("<td>").append(escape(item.getDescription())).append("</td>")
                .append("<td class='text-center'>").append(item.getQuantity()).append("</td>")
                .append("<td class='text-right'>").append(currency(item.getUnitPrice())).append("</td>")
                .append("<td class='text-right'>").append(currency(item.getLineTotal())).append("</td>")
                .append("</tr>");
        }

        String escapedName = escape(invoice.getCustomerName());
        String escapedEmail = escape(invoice.getCustomerEmail());

        return String.format("""
<!DOCTYPE html>
<html>
<head>
  <meta charset='utf-8'/>
  <style>
    @page {
      margin: 0.5in;
      size: A4;
    }
    body { 
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
      margin: 0; 
      padding: 0; 
      background: white;
      color: #333;
      line-height: 1.4;
    }
    .container { 
      max-width: 100%%; 
      margin: 0 auto; 
      background: white;
    }
    .letterhead {
      background: linear-gradient(135deg, #1a4d2e 0%%, #2c5530 100%%);
      color: white;
      padding: 40px 30px;
      text-align: center;
      margin-bottom: 30px;
      border-bottom: 4px solid #4a7c59;
      box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    }
    .company-name {
      font-size: 32px;
      font-weight: bold;
      margin-bottom: 8px;
      text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
      letter-spacing: 1px;
    }
    .company-tagline {
      font-size: 16px;
      margin-bottom: 20px;
      font-style: italic;
      opacity: 0.9;
    }
    .company-details {
      font-size: 13px;
      line-height: 1.6;
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      max-width: 600px;
      margin: 0 auto;
    }
    .company-left, .company-right {
      text-align: left;
    }
    .company-section {
      margin-bottom: 15px;
    }
    .company-section strong {
      display: block;
      font-size: 14px;
      margin-bottom: 5px;
      color: #e8f5e8;
    }
    .company-section span {
      font-size: 12px;
      opacity: 0.9;
    }
    .invoice-header {
      display: table;
      width: 100%%;
      margin-bottom: 30px;
      padding: 25px;
      background: #f8f9fa;
      border-left: 6px solid #2c5530;
      border-radius: 0 8px 8px 0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    .invoice-left {
      display: table-cell;
      width: 50%%;
      vertical-align: top;
    }
    .invoice-right {
      display: table-cell;
      width: 50%%;
      vertical-align: top;
      text-align: right;
    }
    .invoice-title {
      font-size: 28px;
      font-weight: bold;
      color: #2c5530;
      margin-bottom: 15px;
      text-transform: uppercase;
      letter-spacing: 1px;
    }
    .invoice-details {
      font-size: 14px;
      color: #666;
      line-height: 1.5;
    }
    .invoice-details strong {
      color: #2c5530;
    }
    .customer-info {
      background: #ffffff;
      padding: 20px;
      border-left: 4px solid #4a7c59;
      border-radius: 0 6px 6px 0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    .customer-info h3 {
      margin: 0 0 15px 0;
      color: #2c5530;
      font-size: 18px;
      font-weight: bold;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    .customer-info p {
      margin: 8px 0;
      font-size: 14px;
      line-height: 1.4;
    }
    .customer-info strong {
      color: #1a4d2e;
      font-weight: 600;
    }
    .items-table {
      width: 100%%;
      border-collapse: collapse;
      margin: 30px 0;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
      border-radius: 8px;
      overflow: hidden;
    }
    .items-table th {
      background: linear-gradient(135deg, #2c5530 0%%, #1a4d2e 100%%);
      color: white;
      padding: 15px 12px;
      text-align: left;
      font-weight: bold;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    .items-table td {
      padding: 12px;
      border-bottom: 1px solid #e9ecef;
      font-size: 13px;
      vertical-align: top;
    }
    .items-table tr:nth-child(even) {
      background: #f8f9fa;
    }
    .items-table tr:hover {
      background: #e8f5e8;
    }
    .text-right { text-align: right; }
    .text-center { text-align: center; }
    .totals-section {
      background: linear-gradient(135deg, #f8f9fa 0%%, #e9ecef 100%%);
      padding: 25px;
      margin-top: 30px;
      border: 2px solid #dee2e6;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    .totals {
      text-align: right;
      font-size: 16px;
    }
    .totals div {
      margin: 10px 0;
      padding: 8px 0;
    }
    .total-amount {
      font-size: 20px;
      font-weight: bold;
      color: #2c5530;
      border-top: 3px solid #2c5530;
      padding-top: 15px;
      margin-top: 15px;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    .footer {
      margin-top: 50px;
      padding: 30px;
      background: linear-gradient(135deg, #f8f9fa 0%%, #e9ecef 100%%);
      text-align: center;
      font-size: 13px;
      color: #666;
      border-top: 4px solid #2c5530;
      border-radius: 8px 8px 0 0;
    }
    .footer p {
      margin: 8px 0;
      line-height: 1.5;
    }
    .footer strong {
      color: #2c5530;
      font-weight: 600;
    }
    .thank-you {
      font-style: italic;
      color: #2c5530;
      font-weight: bold;
      margin-top: 20px;
      font-size: 14px;
    }
    .contact-info {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #dee2e6;
    }
    .contact-section {
      text-align: left;
    }
    .contact-section strong {
      display: block;
      color: #2c5530;
      margin-bottom: 5px;
    }
  </style>
  <title>Invoice #%d - Galle My Bungalow</title>
</head>
<body>
  <div class='container'>
    <div class='letterhead'>
      <div class='company-name'>Galle My Bungalow</div>
      <div class='company-tagline'>Established Since 1938</div>
      <div class='company-details'>
        <div class='company-left'>
          <div class='company-section'>
            <strong>Address</strong>
            <span>165, Bandaranayake Place<br />Galle, Sri Lanka</span>
          </div>
          <div class='company-section'>
            <strong>Contact</strong>
            <span>Phone: 0713178806</span>
          </div>
        </div>
        <div class='company-right'>
          <div class='company-section'>
            <strong>Owner</strong>
            <span>Mr. Ranjith Kalansuriya</span>
          </div>
          <div class='company-section'>
            <strong>Manager</strong>
            <span>Odevnee Kalansuriya</span>
          </div>
        </div>
      </div>
    </div>

    <div class='invoice-header'>
      <div class='invoice-left'>
        <div class='invoice-title'>INVOICE</div>
        <div class='invoice-details'>
          <strong>Invoice #:</strong> %d<br />
          <strong>Date:</strong> %s<br />
          <strong>Status:</strong> Due
        </div>
      </div>
      <div class='invoice-right'>
        <div class='customer-info'>
          <h3>Bill To:</h3>
          <p><strong>%s</strong></p>
          <p>%s</p>
        </div>
      </div>
    </div>

    <table class='items-table'>
      <thead>
        <tr>
          <th style='width: 50%%'>Description</th>
          <th class='text-center' style='width: 15%%'>Qty</th>
          <th class='text-right' style='width: 17.5%%'>Unit Price</th>
          <th class='text-right' style='width: 17.5%%'>Amount</th>
        </tr>
      </thead>
      <tbody>
        %s
      </tbody>
    </table>

    <div class='totals-section'>
      <div class='totals'>
        <div><strong>Subtotal:</strong> %s</div>
        <div class='total-amount'>Total: %s</div>
      </div>
    </div>

    <div class='footer'>
      <p><strong>Thank you for choosing Galle My Bungalow!</strong></p>
      <div class='contact-info'>
        <div class='contact-section'>
          <strong>For Inquiries:</strong>
          <span>Phone: 0713178806</span>
        </div>
        <div class='contact-section'>
          <strong>Address:</strong>
          <span>165, Bandaranayake Place, Galle</span>
        </div>
      </div>
      <div class='thank-you'>We appreciate your business and look forward to serving you again!</div>
    </div>
  </div>
</body>
</html>
""", 
                invoice.getId(),
                invoice.getId(),
                invoice.getIssuedAt() != null ? invoice.getIssuedAt().toLocalDate().toString() : "N/A",
                escapedName,
                escapedEmail,
                rows.toString(),
                currency(invoice.getSubtotal()),
                currency(invoice.getTotal())
        );
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        List<InvoiceItemResponse> items = (invoice.getItems() == null ? List.<InvoiceItemResponse>of() : invoice.getItems().stream().map(ii ->
                InvoiceItemResponse.builder()
                        .id(ii.getId())
                        .description(ii.getDescription())
                        .quantity(ii.getQuantity())
                        .unitPrice(ii.getUnitPrice())
                        .lineTotal(ii.getLineTotal())
                        .build()
        ).toList());

        // Handle remarks safely - might be null if database doesn't have the column yet
        String remarks = null;
        try {
            remarks = invoice.getRemarks();
        } catch (Exception e) {
            log.warn("Could not access remarks field for invoice {}: {}", invoice.getId(), e.getMessage());
            remarks = null;
        }
        
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .customerId(invoice.getCustomerId())
                .customerName(invoice.getCustomerName())
                .customerEmail(invoice.getCustomerEmail())
                .issuedAt(invoice.getIssuedAt())
                .subtotal(invoice.getSubtotal())
                .total(invoice.getTotal())
                .remarks(remarks)
                .items(items)
                .build();
    }


}
