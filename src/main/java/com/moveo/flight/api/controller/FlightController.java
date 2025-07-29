package com.moveo.flight.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moveo.flight.api.model.Flight;
import com.moveo.flight.api.repository.FlightRepository;
import com.moveo.flight.api.service.FlightService;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    @Autowired
    private FlightRepository flightRepository;
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    /*
     * Get all flights
     */
    @GetMapping
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    /*
     * Search flights by criteria
     */
    @PostMapping
    public Flight createFlight(@RequestBody Flight flight) {
        return flightRepository.save(flight);
    }

    /*
     * Get flight by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        return flightRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /*
     * Update specific flight by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long id, @RequestBody Flight updatedFlight) {
        return flightRepository.findById(id).map(flight -> {
                    flight.setModel(updatedFlight.getModel());
                    flight.setCapacity(updatedFlight.getCapacity());
                    flight.setStatus(updatedFlight.getStatus());
                    flight.setPricePerHour(updatedFlight.getPricePerHour());
                    flight.setImages(updatedFlight.getImages());
                    return ResponseEntity.ok(flightRepository.save(flight));
                }).orElse(ResponseEntity.notFound().build());
    }

    /*
     * Delete flight by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        return flightRepository.findById(id).map(flight -> {
                    flightRepository.delete(flight);
                    return ResponseEntity.noContent().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint de recherche de vols
     * Ex: /api/flights/search?model=Boeing&status=AVAILABLE&minPrice=2000&maxPrice=5000
     */
    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        List<Flight> results = flightService.searchFlights(model, status, minPrice, maxPrice);
        return ResponseEntity.ok(results);
    }
}
