package com.example.rest_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.rest_api.model.Flight;
import com.example.rest_api.repository.FlightRepository;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    @Autowired
    private FlightRepository flightRepository;

    @GetMapping
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    @PostMapping
    public Flight createFlight(@RequestBody Flight flight) {
        return flightRepository.save(flight);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long id) {
        return flightRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long id) {
        return flightRepository.findById(id).map(flight -> {
                    flightRepository.delete(flight);
                    return ResponseEntity.noContent().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}
