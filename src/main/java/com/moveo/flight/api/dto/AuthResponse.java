package com.moveo.flight.api.dto;

/**
 * DTO for login response, returning JWT token.
 */
public class AuthResponse {

    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
