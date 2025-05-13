package com.wehaul.model;

import java.math.BigDecimal; // Use BigDecimal for currency

public class TruckType {
    private int typeId;
    private String name;
    private String description;
    private BigDecimal dailyRate; // Use BigDecimal for precise money handling
    private String capacity;

    // Default constructor
    public TruckType() {
    }

    // Constructor with all fields
    public TruckType(int typeId, String name, String description, BigDecimal dailyRate, String capacity) {
        this.typeId = typeId;
        this.name = name;
        this.description = description;
        this.dailyRate = dailyRate;
        this.capacity = capacity;
    }

    // Getters and Setters
    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "TruckType{" +
                "typeId=" + typeId +
                ", name='" + name + '\'' +
                ", dailyRate=" + dailyRate +
                ", capacity='" + capacity + '\'' +
                '}';
    }
}
