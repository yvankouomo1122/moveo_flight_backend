package com.moveo.flight.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.moveo.flight.api.security.CustomUserDetailsService;
import com.moveo.flight.api.security.JwtAuthenticationFilter;
import com.moveo.flight.api.security.JwtUtil;

/**
 * SecurityConfig defines which endpoints are public
 * and which require authentication.
 * 
 * For now, we allow free access to user registration
 * and disable CSRF for API usage.
 */
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // ✅ Create JWT filter
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(jwtUtil, userDetailsService);

        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Everyone endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/flights/search").permitAll()
                .requestMatchers("/api/users/verify-otp").permitAll()
                .requestMatchers("/api/users/resend-otp").permitAll()
                // Admin-only endpoints
                .requestMatchers(HttpMethod.GET, "/api/reservations").hasRole("ADMIN")
                .requestMatchers("/api/flights/**").hasRole("ADMIN")
                // Payment verificaiton endpoint
                .requestMatchers(HttpMethod.POST, "/api/payments/**").permitAll()
                .requestMatchers("/api/payments/stripe/webhook").permitAll()
                // Everything else needs authentication
                .anyRequest().authenticated()
            )
            // ✅ Add JWT filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

