package com.backend.mybungalow.repository;

import com.backend.mybungalow.model.Requests;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Requests, Long> {
    List<Requests> findByCustomerEmail(String email);
}
