package com.moveo.flight.api.repository;

import com.moveo.flight.api.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for User entity.
 * 
 * Provides:
 *  - CRUD operations
 *  - Custom query to find by email
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find a user by email (for login)
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if an email already exists
     */
    boolean existsByEmail(String email);
}
