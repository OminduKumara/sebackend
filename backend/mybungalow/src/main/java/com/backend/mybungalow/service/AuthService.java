package com.backend.mybungalow.service;

import com.backend.mybungalow.dto.AuthResponse;
import com.backend.mybungalow.dto.LoginRequest;
import com.backend.mybungalow.dto.RegisterRequest;
import com.backend.mybungalow.model.User;

public interface AuthService {
    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);
    User getCurrentUser();
}
