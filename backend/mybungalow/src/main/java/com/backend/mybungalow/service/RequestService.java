package com.backend.mybungalow.service;

import com.backend.mybungalow.model.Requests;
import com.backend.mybungalow.repository.RequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestService {
    private final RequestRepository repository;

    public RequestService(RequestRepository repository) {
        this.repository = repository;
    }

    // Customer
    public Requests sendRequests(Requests msg) {
        return repository.save(msg);
    }

    public List<Requests> getRequestsByCustomer(String email) {
        return repository.findByCustomerEmail(email);
    }

    // Admin
    public List<Requests> getAllRequests() {
        return repository.findAll();
    }

    public Requests markAsDone(Long id) {
        Requests msg = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requests not found"));
        msg.setDone(true);
        return repository.save(msg);
    }
}
