package com.backend.mybungalow.repository;

import com.backend.mybungalow.model.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByCustomerIdOrderByIssuedAtDesc(Long customerId);
    
    // Admin methods for viewing all invoices
    Page<Invoice> findAllByOrderByIssuedAtDesc(Pageable pageable);
    
    @Query("SELECT i FROM Invoice i WHERE " +
           "(:customerName IS NULL OR LOWER(i.customerName) LIKE LOWER(CONCAT('%', :customerName, '%'))) AND " +
           "(:customerEmail IS NULL OR LOWER(i.customerEmail) LIKE LOWER(CONCAT('%', :customerEmail, '%'))) AND " +
           "(:startDate IS NULL OR i.issuedAt >= :startDate) AND " +
           "(:endDate IS NULL OR i.issuedAt <= :endDate)")
    Page<Invoice> findInvoicesWithFilters(
        @Param("customerName") String customerName,
        @Param("customerEmail") String customerEmail,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
}



