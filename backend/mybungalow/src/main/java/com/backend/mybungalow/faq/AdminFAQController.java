package com.backend.mybungalow.faq;

import com.backend.mybungalow.faq.dto.CreateFAQRequest;
import com.backend.mybungalow.faq.dto.FAQResponse;
import com.backend.mybungalow.faq.dto.UpdateFAQRequest;
import com.backend.mybungalow.faq.FAQService;
import com.backend.mybungalow.faq.dto.CreateFAQRequest;
import com.backend.mybungalow.faq.dto.FAQResponse;
import com.backend.mybungalow.faq.dto.UpdateFAQRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/FAQs")
public class AdminFAQController {
    private final FAQService FAQService;

    public AdminFAQController(FAQService FAQService) {
        this.FAQService = FAQService;
    }

    @GetMapping
    public ResponseEntity<List<com.backend.mybungalow.faq.dto.FAQResponse>> getAllFAQs() {
        return ResponseEntity.ok(FAQService.getAllFaqs());
    }

    @PostMapping
    public ResponseEntity<com.backend.mybungalow.faq.dto.FAQResponse> createFAQ(@Valid @RequestBody com.backend.mybungalow.faq.dto.CreateFAQRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(FAQService.createFaq(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<com.backend.mybungalow.faq.dto.FAQResponse> updateFAQ(@PathVariable Long id, @Valid @RequestBody com.backend.mybungalow.faq.dto.UpdateFAQRequest request) {
        return ResponseEntity.ok(FAQService.updateFaq(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFAQ(@PathVariable Long id) {
        FAQService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}

 