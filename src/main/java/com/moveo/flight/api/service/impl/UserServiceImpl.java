package com.moveo.flight.api.service.impl;

import com.moveo.flight.api.model.User;
import com.moveo.flight.api.model.UserStatus;
import com.moveo.flight.api.repository.UserRepository;
import com.moveo.flight.api.service.EmailService;
import com.moveo.flight.api.service.SmsService;
import com.moveo.flight.api.service.UserService;
import com.moveo.flight.api.service.OtpService;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final EmailService emailService;
    private final SmsService smsService;
    protected final OtpService otpService;

    // Constructor injection for UserRepository, EmailService, SmsService, and OtpService
    public UserServiceImpl(UserRepository userRepository, EmailService emailService, SmsService smsService, OtpService otpService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.smsService = smsService;
        this.otpService = otpService;
    }

    /**
     * Create a new user, checking email uniqueness.
     * @throws MessagingException if email sending fails
     */
    @Override
    public boolean createUser(User user) throws MessagingException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // Create user with PENDING_VERIFICATION status
        User u = new User();
        u.setEmail(user.getEmail());
        u.setPhoneNumber(user.getPhoneNumber());
        u.setRoles(user.getRoles());
        u.setFullName(user.getFullName());
        u.setPassword(passwordEncoder.encode(user.getPassword())); // Hash the password before saving
        u.setStatus(UserStatus.PENDING_VERIFICATION); // Set initial status

        // Generate OTP
        String otp = String.format("%06d", (int) (Math.random() * 1000000));
        u.setOtpCode(otp);
        u.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // Set OTP expiry to 5 minutes from now

        // Send OTP via email and SMS
        boolean sent = otpService.sendOtp(u, otp);
        boolean user_created;
        if(!sent) {
            user_created = false;
            return user_created; // Return false if OTP sending failed
        }
        user_created = true;
        userRepository.save(u);
        return user_created; // Return true if user created successfully
    }

    @Override
    public boolean verifyOtp(String email, String otpCode) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (u.getOtpExpiry().isBefore(LocalDateTime.now())){
            return false; // OTP expired
        }
        if (!u.getOtpCode().equals(otpCode)) {
            return false; // Invalid OTP
        }

        u.setStatus(UserStatus.ACTIVE); // Set user status to ACTIVE
        u.setOtpCode(null); // Clear OTP code after verification
        u.setOtpExpiry(null); // Clear OTP expiry after verification
        userRepository.save(u); // Save updated user status
        return true; // OTP verified successfully
    }

    /*
     * Resend OTP to user
     */
    @Override
    public void resendOtp(String email) throws MessagingException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (u.getStatus() == UserStatus.ACTIVE) {
            throw new IllegalArgumentException("Invalid or expired OTP.");
        }
        // Generate new OTP
        String newOtp = String.format("%06d", (int) (Math.random() * 1000000));
        u.setOtpCode(newOtp);
        u.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // Reset OTP expiry to 5 minutes from now
        userRepository.save(u);

        // Send new OTP via email and SMS
        String message = "Your new OTP for verification is: " + newOtp;
        // smsService.sendSms(u.getPhoneNumber(), message);
        emailService.sendEmail(email, "OTP Verification", message);
    }

    /**
     * Retrieve a user by ID.
     */
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    /**
     * Fetch all users.
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /*
     * Update an existing user by ID. (except password)
     */
    @Override
    public User updateUser(Long id, User updatedUser) {
        // Ensure the user exists before updating
        User existingUser = getUserById(id);
        // Update fullname, phone number, and roles
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setRoles(updatedUser.getRoles());
        return userRepository.save(existingUser);
    }

    /**
     * Delete a user by ID.
     */
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
