package com.backend.mybungalow.customer;

import com.backend.mybungalow.customer.dto.CustomerResponse;
import com.backend.mybungalow.customer.dto.RegisterCustomerRequest;
import com.backend.mybungalow.customer.dto.CustomerLoginRequest;
import com.backend.mybungalow.customer.dto.CustomerAuthResponse;
import org.springframework.dao.DataIntegrityViolationException;
import com.backend.mybungalow.security.JwtUtil;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;

    public CustomerService(CustomerRepository customerRepository, JwtUtil jwtUtil) {
        this.customerRepository = customerRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public CustomerResponse registerCustomer(RegisterCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new DataIntegrityViolationException("Email already registered");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        String salt = BCrypt.gensalt(10);
        String hash = BCrypt.hashpw(request.getPassword(), salt);
        customer.setPasswordHash(hash);

        Customer saved = customerRepository.save(customer);
        return new CustomerResponse(saved.getId(), saved.getFirstName(), saved.getLastName(), saved.getEmail(), saved.getPhone(), saved.getCreatedAt(), saved.getUpdatedAt());
    }

    public CustomerAuthResponse loginCustomer(CustomerLoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!BCrypt.checkpw(request.password(), customer.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate a JWT with subject as customer email and a claim to mark it as customer
        String token = jwtUtil.generateTokenFromSubject(customer.getEmail(), Map.of("type", "customer", "cid", customer.getId()));
        return CustomerAuthResponse.of(token, customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> new CustomerResponse(
                        customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getCreatedAt(),
                        customer.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> searchCustomers(String query) {
        return customerRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, query).stream()
                .map(customer -> new CustomerResponse(
                        customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        customer.getPhone(),
                        customer.getCreatedAt(),
                        customer.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found");
        }
        customerRepository.deleteById(id);
    }
}


