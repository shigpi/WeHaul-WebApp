package com.wehaul.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RentalOrder {
    private Integer orderId;
    private Integer customerId;
    private Integer truckId; 
    private Integer assignedTruckId; 
    private Timestamp pickupDate;
    private Timestamp returnDate;
    private String pickupLocation;
    private String returnLocation;
    private String status;
    private String approvalStatus = "pending";
    private Integer assignedAdminId;
    private BigDecimal totalCost;
    private Timestamp createdAt;
    private String nepaliPickupLocation;
    private String nepaliReturnLocation;
    
    private Customer customer;
    private TruckType truckTypeInfo;
    private Truck assignedTruckInfo;

    public RentalOrder() {}
    
    // Getters and Setters
	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getTruckId() {
		return truckId;
	}

	public void setTruckId(Integer truckId) {
		this.truckId = truckId;
	}

	public Integer getAssignedTruckId() {
		return assignedTruckId;
	}

	public void setAssignedTruckId(Integer assignedTruckId) {
		this.assignedTruckId = assignedTruckId;
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

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
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

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public TruckType getTruckTypeInfo() {
		return truckTypeInfo;
	}

	public void setTruckTypeInfo(TruckType truckTypeInfo) {
		this.truckTypeInfo = truckTypeInfo;
	}

	public Truck getAssignedTruckInfo() {
		return assignedTruckInfo;
	}

	public void setAssignedTruckInfo(Truck assignedTruckInfo) {
		this.assignedTruckInfo = assignedTruckInfo;
	}
    
}