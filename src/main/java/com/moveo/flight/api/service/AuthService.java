package com.moveo.flight.api.service;

import com.moveo.flight.api.dto.AuthRequest;
import com.moveo.flight.api.dto.AuthResponse;
import com.moveo.flight.api.model.User;
import com.moveo.flight.api.repository.UserRepository;
import com.moveo.flight.api.security.JwtUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handles user authentication logic (login).
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticate user and return JWT token.
     */
    public AuthResponse login(AuthRequest request) {
        Optional<User> u = userRepository.findByEmail(request.getEmail());
        User user = u.get();

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Generate JWT token
        String token = JwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}

