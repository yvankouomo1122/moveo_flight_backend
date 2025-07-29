package com.moveo.flight.api.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.moveo.flight.api.repository.FlightRepository;
import com.moveo.flight.api.model.Flight;
import com.moveo.flight.api.model.FlightStatus;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /**
     * Flight search by criteria (model, status, min/max price)
     */
    public List<Flight> searchFlights(String model, String status, Double minPrice, Double maxPrice) {
        FlightStatus flightStatus = null;
        if (status != null) {
            flightStatus = FlightStatus.valueOf(status.toUpperCase());
        }
        return flightRepository.searchFlights(model, flightStatus, minPrice, maxPrice);
    }
}

