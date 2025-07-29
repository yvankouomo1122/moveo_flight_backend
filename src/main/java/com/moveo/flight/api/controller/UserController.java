package com.moveo.flight.api.controller;

import com.moveo.flight.api.model.User;
import com.moveo.flight.api.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.moveo.flight.api.dto.VerifyOtpDTO;

/**
 * REST Controller for User Management.
 * Endpoints for CRUD operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user
     * @throws MessagingException 
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) throws MessagingException {
        boolean user_created = userService.createUser(user);
        if (user_created){
            return ResponseEntity.ok("User registered. OTP sent to phone/email for verification.");
        } else {
            return ResponseEntity.status(500).body("Failed to send OTP. Please try again later.");
        }
    }

    /*
     * Verify user with OTP
     */

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpDTO req){
        boolean verified = userService.verifyOtp(req.getEmail(), req.getOtpCode());
        if (verified){
            return ResponseEntity.ok("User verified successfully.");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP.");
        }
    }
    
    @GetMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestParam String email) throws MessagingException {
        userService.resendOtp(email);
        return ResponseEntity.ok("OTP resent to " + email);
    }

    /**
     * Get a single user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * List all users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Update user details (except password)
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return ResponseEntity.ok(userService.updateUser(id, updatedUser));
    }

    /**
     * Delete a user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}