package com.moveo.flight.api.service;

import com.moveo.flight.api.model.ReservationStatus;
import com.moveo.flight.api.dto.ReservationDTO;
import com.moveo.flight.api.model.Flight;
import com.moveo.flight.api.model.Reservation;
import com.moveo.flight.api.model.User;
import com.moveo.flight.api.repository.FlightRepository;
import com.moveo.flight.api.repository.ReservationRepository;
import com.moveo.flight.api.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing reservations.
 * Contains the main business logic.
 */
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            FlightRepository flightRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
    }

    /** Create a new reservation */
    public Reservation createReservation(Long userId, Long flightId, Reservation reservation) {
        // Fetch user and flight
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        // Set relations
        reservation.setUser(user);
        reservation.setFlight(flight);

        // Compute total price = price per hour * duration
        long hours = java.time.Duration.between(reservation.getStartDate(), reservation.getEndDate()).toHours();
        Long total = flight.getPricePerHour() * hours;
        reservation.setTotalPrice(total);

        reservation.setStatus(ReservationStatus.PENDING);

        return reservationRepository.save(reservation);
    }

    /** Get reservation by ID */
    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reservation not found with id " + id));
    }

    /** Get all reservations for a user */
    public List<ReservationDTO> getReservationsByUser(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return reservations.stream()
                .map(this::toDTO)
                .toList(); // Convert Entity to DTO
    }

    /** Cancel a reservation */
    public ReservationDTO cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());
        Reservation saved = reservationRepository.save(reservation);
        return toDTO(saved);
    }

    /** Get all reservations (admin use) */
    public List<ReservationDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::toDTO)
                .toList(); // Convert Entity to DTO
    }

    public ReservationDTO toDTO(Reservation reservation) {
    User user = reservation.getUser();
    Flight flight = reservation.getFlight();

    return new ReservationDTO(
        reservation.getId(),
        reservation.getStartDate(),
        reservation.getEndDate(),
        reservation.getTotalPrice(),
        reservation.getStatus().name(),
        (user != null) ? user.getId() : null,
        (user != null) ? user.getEmail() : null,
        (flight != null) ? flight.getId() : null,
        (flight != null) ? flight.getModel() : null
    );
    }
}
