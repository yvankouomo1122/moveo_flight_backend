package com.moveo.flight.api.repository;

import com.moveo.flight.api.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for Reservation entity.
 * Provides CRUD operations and custom queries.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // Get all reservations for a specific user
    List<Reservation> findByUserId(Long userId);

    // Get all reservations for a specific flight
    List<Reservation> findByFlightId(Long flightId);
}
