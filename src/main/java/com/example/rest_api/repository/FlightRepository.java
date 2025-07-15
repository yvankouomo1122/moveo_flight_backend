package com.example.rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.rest_api.model.Flight;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    // Additional query methods can be defined here if needed
    
}