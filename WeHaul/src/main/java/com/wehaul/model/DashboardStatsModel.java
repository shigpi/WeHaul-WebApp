package com.wehaul.model;

public class DashboardStatsModel {
    private int totalCustomers;
    private int availableTrucks;
    private int activeRentals;
    private int pendingOrders;
    
    public DashboardStatsModel(int totalCustomers, int availableTrucks, int activeRentals, int pendingOrders) {
        this.totalCustomers = totalCustomers;
        this.availableTrucks = availableTrucks;
        this.activeRentals = activeRentals;
        this.pendingOrders = pendingOrders;
    }
    
    public DashboardStatsModel() {
    	
    }

    //Getters and Setters
	public int getTotalCustomers() {
		return totalCustomers;
	}

	public void setTotalCustomers(int totalCustomers) {
		this.totalCustomers = totalCustomers;
	}

	public int getAvailableTrucks() {
		return availableTrucks;
	}

	public void setAvailableTrucks(int availableTrucks) {
		this.availableTrucks = availableTrucks;
	}

	public int getActiveRentals() {
		return activeRentals;
	}

	public void setActiveRentals(int activeRentals) {
		this.activeRentals = activeRentals;
	}

	public int getPendingOrders() {
		return pendingOrders;
	}

	public void setPendingOrders(int pendingOrders) {
		this.pendingOrders = pendingOrders;
	}
    
    
}

