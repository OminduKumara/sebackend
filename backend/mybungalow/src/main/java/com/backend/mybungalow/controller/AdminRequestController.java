package com.backend.mybungalow.controller;

import com.backend.mybungalow.model.Requests;
import com.backend.mybungalow.service.RequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/Requests")
@CrossOrigin(origins = "*")
public class AdminRequestController {
    private final RequestService RequestService;

    public AdminRequestController(RequestService RequestService) {
        this.RequestService = RequestService;
    }

    @GetMapping
    public List<Requests> getAllRequests() {
        return RequestService.getAllRequests();
    }

    @PutMapping("/{id}/done")
    public Requests markRequestAsDone(@PathVariable Long id) {
        return RequestService.markAsDone(id);
    }
}
