package com.example.rest_api.model;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;

@Entity
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String model;
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.AVAILABLE;
    private Double pricePerHour;

    @Column(columnDefinition = "TEXT")
    private String images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public String getModel() {
        return model;
    }
    public FlightStatus getStatus() {
        return status;
    }
    public Double getPricePerHour() {
        return pricePerHour;
    }
    public String getImages() {
        return images;
    }
    public Integer getCapacity() {
        return capacity;
    }
    public void setModel(String model) {
        this.model = model;
    }
    public void setStatus(FlightStatus status) {
        this.status = status;
    }
    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
    public void setImages(String images) {
        this.images = images;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

}
