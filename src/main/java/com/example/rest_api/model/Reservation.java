package com.example.rest_api.model;

import jakarta.persistence.*;

@Entity
public class Reservation {
    @Id
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight;

    // Adding other fields

    // Getters and Setters
}
