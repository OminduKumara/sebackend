package com.backend.mybungalow.controller;

import com.backend.mybungalow.model.Requests;
import com.backend.mybungalow.service.RequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/Requests")
@CrossOrigin(origins = "*")
public class CustomerRequestController {
    private final RequestService RequestService;

    public CustomerRequestController(RequestService RequestService) {
        this.RequestService = RequestService;
    }

    @PostMapping
    public Requests sendRequest(@RequestBody Requests Request) {
        return RequestService.sendRequests(Request);
    }

    @GetMapping
    public List<Requests> getMyRequests(@RequestParam String email) {
        return RequestService.getRequestsByCustomer(email);
    }
}
