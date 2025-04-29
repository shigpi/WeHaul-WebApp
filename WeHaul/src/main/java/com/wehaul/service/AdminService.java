package com.wehaul.service;

import com.wehaul.config.DbConfig;
import com.wehaul.model.Truck;
import com.wehaul.model.Customer;
import com.wehaul.model.RentalOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public List<Truck> getAllTrucks() throws SQLException, ClassNotFoundException {
        List<Truck> trucks = new ArrayList<>();
        Connection conn = DbConfig.getDbConnection();

        String sql = "SELECT * FROM trucks";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Truck truck = new Truck();
            truck.setTruckId(rs.getInt("truck_id"));
            truck.setTypeId(rs.getInt("type_id"));
            truck.setTypeName(rs.getString("type_name"));
            truck.setLicensePlate(rs.getString("license_plate"));
            truck.setMake(rs.getString("make"));
            truck.setModel(rs.getString("model"));
            truck.setYear(rs.getInt("year"));
            truck.setMileage(rs.getInt("mileage"));
            truck.setLastMaintenanceDate(rs.getDate("last_maintenance_date"));
            truck.setStatus(rs.getString("status"));
            truck.setCurrentLocation(rs.getString("current_location"));
            truck.setDailyRate(rs.getBigDecimal("daily_rate"));
            truck.setCapacity(rs.getString("capacity"));
            trucks.add(truck);
        }

        rs.close();
        stmt.close();
        conn.close();
        return trucks;
    }

    public List<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
        List<Customer> customers = new ArrayList<>();
        Connection conn = DbConfig.getDbConnection();

        String sql = "SELECT * FROM customers";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            Customer customer = new Customer();
            customer.setCustomerId(rs.getInt("customer_id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));
            customer.setEmail(rs.getString("email"));
            customer.setPhone(rs.getString("phone"));
            customer.setAddress(rs.getString("address"));
            customer.setCity(rs.getString("city"));
            customer.setState(rs.getString("state"));
            customer.setPasswordHash(rs.getString("password_hash"));
            customer.setCreatedAt(rs.getTimestamp("created_at"));
            customer.setUpdatedAt(rs.getTimestamp("updated_at"));
            customers.add(customer);
        }

        rs.close();
        stmt.close();
        conn.close();
        return customers;
    }

    public List<RentalOrder> getAllOrders() throws SQLException, ClassNotFoundException {
        List<RentalOrder> orders = new ArrayList<>();
        Connection conn = DbConfig.getDbConnection();

        String sql = "SELECT * FROM rental_orders";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            RentalOrder order = new RentalOrder();
            order.setOrderId(rs.getInt("order_id"));
            order.setCustomerId(rs.getInt("customer_id"));
            order.setTruckId(rs.getInt("truck_id"));
            order.setPickupDate(rs.getTimestamp("pickup_date"));
            order.setReturnDate(rs.getTimestamp("return_date"));
            order.setPickupLocation(rs.getString("pickup_location"));
            order.setReturnLocation(rs.getString("return_location"));
            order.setStatus(rs.getString("status"));
            order.setApprovalStatus(rs.getString("approval_status"));
            order.setAssignedAdminId((Integer) rs.getObject("assigned_admin_id"));
            order.setAssignedTruckId((Integer) rs.getObject("assigned_truck_id"));
            order.setTotalCost(rs.getDouble("total_cost"));
            order.setCreatedAt(rs.getTimestamp("created_at"));
            order.setNepaliPickupLocation(rs.getString("nepali_pickup_location"));
            order.setNepaliReturnLocation(rs.getString("nepali_return_location"));
            orders.add(order);
        }

        rs.close();
        stmt.close();
        conn.close();
        return orders;
    }

    public void approveRentalOrder(int orderId, int adminId, int truckId) throws SQLException, ClassNotFoundException {
        Connection conn = DbConfig.getDbConnection();

        String sql = "UPDATE rental_orders SET status = ?, approval_status = ?, assigned_admin_id = ?, assigned_truck_id = ? WHERE order_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "confirmed");
        stmt.setString(2, "approved");
        stmt.setInt(3, adminId);
        stmt.setInt(4, truckId);
        stmt.setInt(5, orderId);

        stmt.executeUpdate();

        stmt.close();
        conn.close();
    }
}

