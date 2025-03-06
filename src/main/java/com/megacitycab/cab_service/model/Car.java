package com.megacitycab.cab_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Car number is required")
    private String carNumber;
    @NotBlank(message = "Model is required")
    private String model;
    private boolean available;

    public Car() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCarNumber() { return carNumber; }
    public void setCarNumber(String carNumber) { this.carNumber = carNumber; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}