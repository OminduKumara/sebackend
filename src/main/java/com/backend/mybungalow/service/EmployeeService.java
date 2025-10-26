package com.backend.mybungalow.service;

import com.backend.mybungalow.dto.EmployeeCreateRequest;
import com.backend.mybungalow.dto.EmployeeResponse;
import com.backend.mybungalow.dto.EmployeeUpdateRequest;
import com.backend.mybungalow.dto.EmployeeCreateRequest;
import com.backend.mybungalow.dto.EmployeeResponse;
import com.backend.mybungalow.dto.EmployeeUpdateRequest;

import java.util.List;

public interface EmployeeService {
    List<EmployeeResponse> getAllEmployees();
    EmployeeResponse createEmployee(EmployeeCreateRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request);
    EmployeeResponse getEmployeeByNic(String nic);
    void deleteEmployee(Long id);
}
