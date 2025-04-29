package com.wehaul.model;

import java.sql.Timestamp;

public class RentalOrder {
    private int orderId;
    private int customerId;
    private int truckId;
    private Timestamp pickupDate;
    private Timestamp returnDate;
    private String pickupLocation;
    private String returnLocation;
    private String status;
    private String approvalStatus = "pending"; // pending/approved/rejected
    private Integer assignedAdminId;
    private Integer assignedTruckId;
    private double totalCost;
    private Timestamp createdAt;
    private String nepaliPickupLocation;
    private String nepaliReturnLocation;

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getTruckId() {
        return truckId;
    }

    public void setTruckId(int truckId) {
        this.truckId = truckId;
    }

    public Timestamp getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Timestamp pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Timestamp returnDate) {
        this.returnDate = returnDate;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public void setReturnLocation(String returnLocation) {
        this.returnLocation = returnLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Integer getAssignedAdminId() {
        return assignedAdminId;
    }

    public void setAssignedAdminId(Integer assignedAdminId) {
        this.assignedAdminId = assignedAdminId;
    }

    public Integer getAssignedTruckId() {
        return assignedTruckId;
    }

    public void setAssignedTruckId(Integer assignedTruckId) {
        this.assignedTruckId = assignedTruckId;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getNepaliPickupLocation() {
        return nepaliPickupLocation;
    }

    public void setNepaliPickupLocation(String nepaliPickupLocation) {
        this.nepaliPickupLocation = nepaliPickupLocation;
    }

    public String getNepaliReturnLocation() {
        return nepaliReturnLocation;
    }

    public void setNepaliReturnLocation(String nepaliReturnLocation) {
        this.nepaliReturnLocation = nepaliReturnLocation;
    }
}
