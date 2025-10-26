package com.backend.mybungalow.dto;

import com.backend.mybungalow.model.User;

public record AuthResponse(
    String token,
    String type,
    Long id,
    String username,
    String email,
    User.Role role
) {
    public static AuthResponse of(String token, User user) {
        return new AuthResponse(
            token,
            "Bearer",
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole()
        );
    }
}
