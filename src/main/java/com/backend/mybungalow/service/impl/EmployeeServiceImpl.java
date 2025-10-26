package com.backend.mybungalow.service.impl;

import com.backend.mybungalow.dto.EmployeeCreateRequest;
import com.backend.mybungalow.dto.EmployeeResponse;
import com.backend.mybungalow.dto.EmployeeUpdateRequest;
import com.backend.mybungalow.exception.ResourceNotFoundException;
import com.backend.mybungalow.model.Employee;
import com.backend.mybungalow.model.EmployeeStatus;
import com.backend.mybungalow.repository.EmployeeRepository;
import com.backend.mybungalow.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .nic(request.getNic())
                .address(request.getAddress())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .position(request.getPosition())
                .hireDate(request.getHireDate())
                .role(request.getRole())
                .status(EmployeeStatus.valueOf(request.getStatus()))
                .joinDate(request.getJoinDate())
                .dateOfBirth(request.getDateOfBirth())
                .salary(request.getSalary())
                .build();
        Employee saved = employeeRepository.save(employee);
        return toResponse(saved);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setNic(request.getNic());
        employee.setAddress(request.getAddress());
        employee.setPhone(request.getPhone());
        employee.setDepartment(request.getDepartment());
        employee.setPosition(request.getPosition());
        employee.setHireDate(request.getHireDate());
        employee.setRole(request.getRole());
        employee.setStatus(EmployeeStatus.valueOf(request.getStatus()));
        employee.setJoinDate(request.getJoinDate());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setSalary(request.getSalary());
        Employee updated = employeeRepository.save(employee);
        return toResponse(updated);
    }

    @Override
    public EmployeeResponse getEmployeeByNic(String nic) {
        Employee employee = employeeRepository.findByNic(nic)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with NIC: " + nic));
        return toResponse(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .nic(e.getNic())
                .address(e.getAddress())
                .phone(e.getPhone())
                .department(e.getDepartment())
                .position(e.getPosition())
                .hireDate(e.getHireDate())
                .role(e.getRole())
                .status(e.getStatus().name())
                .joinDate(e.getJoinDate())
                .dateOfBirth(e.getDateOfBirth())
                .salary(e.getSalary())
                .build();
    }
}
