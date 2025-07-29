package com.moveo.flight.api.dto;

import java.time.LocalDateTime;

/**
 * DTO for safely returning Reservation info without causing infinite loops
 * or lazy-loading proxy issues.
 */
public class ReservationDTO {
    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long totalPrice;
    private String status;

    // Minimal user info (avoid deep nesting)
    private Long userId;
    private String userEmail;

    // Minimal flight info
    private Long flightId;
    private String flightModel;

    // Empty constructor for Jackson
    public ReservationDTO() {}

    // Full constructor for easy mapping
    public ReservationDTO(Long id, LocalDateTime startDate, LocalDateTime endDate,
                          Long totalPrice, String status,
                          Long userId, String userEmail,
                          Long flightId, String flightModel) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.userId = userId;
        this.userEmail = userEmail;
        this.flightId = flightId;
        this.flightModel = flightModel;
    }

    // Getters (needed for JSON serialization)
    public Long getId() { return id; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public Long getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public Long getUserId() { return userId; }
    public String getUserEmail() { return userEmail; }
    public Long getFlightId() { return flightId; }
    public String getFlightModel() { return flightModel; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public void setTotalPrice(Long totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public void setFlightModel(String flightModel) { this.flightModel = flightModel; }
}

