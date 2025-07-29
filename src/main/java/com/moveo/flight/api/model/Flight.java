package com.moveo.flight.api.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class Flight {
    /**
     * Flight entity representing aircraft details.
     * - Contains flight model, capacity, status, price, images
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Auto-increment ID
    private String model; // Flight model (e.g. Cessna 172)
    private Integer capacity; // Number of passengers it can carry

    // Flight status enum
    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.AVAILABLE; // Default status is AVAILABLE
    private Long pricePerHour; // Price per hour of flight

    @Column(columnDefinition = "TEXT")
    private String images; // JSON array of image URLs
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Created timestamp
    @Column(name="updated_at")
    private LocalDateTime updatedAt; // Updated timestamp

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Reservation> reservations; // One flight can have many reservations

    // Auto-fill timestamps before saving
    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }
    // Auto-update timestamp before updating
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    // Get id model, status, price, images, capacity
    public Long getId() {
        return id;
    }
    public String getModel() {
        return model;
    }
    public FlightStatus getStatus() {
        return status;
    }
    public Long getPricePerHour() {
        return pricePerHour;
    }
    public String getImages() {
        return images;
    }
    public Integer getCapacity() {
        return capacity;
    }
    // Set model, status, price, images, capacity
    public void setModel(String model) {
        this.model = model;
    }
    public void setStatus(FlightStatus status) {
        this.status = status;
    }
    public void setPricePerHour(Long pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
    public void setImages(String images) {
        this.images = images;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

}
