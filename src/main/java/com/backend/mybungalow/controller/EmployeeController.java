package com.backend.mybungalow.controller;

import com.backend.mybungalow.dto.EmployeeCreateRequest;
import com.backend.mybungalow.dto.EmployeeResponse;
import com.backend.mybungalow.dto.EmployeeUpdateRequest;
import com.backend.mybungalow.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = service.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse created = service.createEmployee(request);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable("id") Long id,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        EmployeeResponse updated = service.updateEmployee(id, request);
        return ResponseEntity.ok(updated);
    }

    // Get employee by NIC instead of ID
    @GetMapping("/nic/{nic}")
    public ResponseEntity<EmployeeResponse> getEmployeeByNic(@PathVariable("nic") String nic) {
        EmployeeResponse resp = service.getEmployeeByNic(nic);
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
