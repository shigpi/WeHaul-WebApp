package com.wehaul.service;

import com.wehaul.config.DbConfig; // Assuming you have this
import com.wehaul.model.Customer;
import com.wehaul.model.RentalOrder;
import com.wehaul.model.Truck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // For simple queries without parameters
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public List<Truck> getAllTrucks() throws SQLException, ClassNotFoundException {
        List<Truck> trucks = new ArrayList<>();
        // SQL query now JOINS with truck_types to get type_name
        String sql = "SELECT t.truck_id, t.type_id, tt.name AS type_name, t.license_plate, t.make, t.model, " +
                     "t.year, t.mileage, t.last_maintenance_date, t.status, t.current_location " +
                     "FROM trucks t " +
                     "JOIN truck_types tt ON t.type_id = tt.type_id " +
                     "ORDER BY t.truck_id ASC";

        // ** TODO: Replace direct JDBC with DAO Pattern **
        try (Connection conn = DbConfig.getDbConnection(); // Use your actual connection method
             Statement stmt = conn.createStatement(); // Can use Statement for simple query
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Truck truck = new Truck();
                truck.setTruckId(rs.getInt("truck_id"));
                truck.setTypeId(rs.getInt("type_id"));
                truck.setTypeName(rs.getString("type_name")); // Get the joined type_name
                truck.setLicensePlate(rs.getString("license_plate"));
                truck.setMake(rs.getString("make"));
                truck.setModel(rs.getString("model"));
                truck.setYear(rs.getInt("year"));
                truck.setMileage(rs.getInt("mileage"));
                truck.setLastMaintenanceDate(rs.getDate("last_maintenance_date"));
                truck.setStatus(rs.getString("status"));
                truck.setCurrentLocation(rs.getString("current_location"));
                trucks.add(truck);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all trucks: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
             System.err.println("Database driver class not found: " + e.getMessage());
             throw new SQLException("Database driver error.", e);
        }
        return trucks;
    }

    public List<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, first_name, last_name, email, phone, address, city, state, is_admin, created_at, updated_at FROM customers ORDER BY last_name, first_name";

        // ** TODO: Replace direct JDBC with DAO Pattern **
        try (Connection conn = DbConfig.getDbConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
                customer.setAdmin(rs.getBoolean("is_admin"));
                customer.setCreatedAt(rs.getTimestamp("created_at"));
                customer.setUpdatedAt(rs.getTimestamp("updated_at"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all customers: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
             System.err.println("Database driver class not found: " + e.getMessage());
             throw new SQLException("Database driver error.", e);
        }
        return customers;
    }

    public List<RentalOrder> getAllOrders() throws SQLException, ClassNotFoundException {
        List<RentalOrder> orders = new ArrayList<>();
        // Example: Join with customers and trucks to get more info. Adjust as needed.
        String sql = "SELECT ro.*, c.first_name, c.last_name, t.license_plate " +
                     "FROM rental_orders ro " +
                     "LEFT JOIN customers c ON ro.customer_id = c.customer_id " +
                     "LEFT JOIN trucks t ON ro.truck_id = t.truck_id " + // Assuming ro.truck_id is the assigned truck
                     "ORDER BY ro.created_at DESC";

        // ** TODO: Replace direct JDBC with DAO Pattern **
        try (Connection conn = DbConfig.getDbConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                RentalOrder order = new RentalOrder();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                // If you have a Customer object inside RentalOrder, populate it
                // order.getCustomer().setFirstName(rs.getString("first_name"));
                // order.getCustomer().setLastName(rs.getString("last_name"));

                if (rs.getObject("truck_id") != null) { // Check for null before getInt
                    order.setAssignedTruckId(rs.getInt("truck_id")); // Assuming assignedTruckId maps to truck_id column
                }
                // If you have a Truck object inside RentalOrder, populate its license plate
                // order.getTruck().setLicensePlate(rs.getString("license_plate"));

                order.setPickupDate(rs.getTimestamp("pickup_date"));
                order.setReturnDate(rs.getTimestamp("return_date"));
                order.setPickupLocation(rs.getString("pickup_location"));
                order.setReturnLocation(rs.getString("return_location"));
                order.setNepaliPickupLocation(rs.getString("nepali_pickup_location"));
                order.setNepaliReturnLocation(rs.getString("nepali_return_location"));
                order.setStatus(rs.getString("status"));
                order.setApprovalStatus(rs.getString("approval_status"));
                if (rs.getObject("assigned_admin_id") != null) {
                     order.setAssignedAdminId(rs.getInt("assigned_admin_id"));
                }
                order.setTotalCost(rs.getBigDecimal("total_cost"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                // If rental_orders has a requested_truck_type_id, fetch it
                // if (rs.getObject("requested_truck_type_id") != null) {
                //     order.setTruckTypeId(rs.getInt("requested_truck_type_id"));
                // }
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
             System.err.println("Database driver class not found: " + e.getMessage());
             throw new SQLException("Database driver error.", e);
        }
        return orders;
    }

    public void approveRentalOrder(int orderId, int adminId, int truckId) throws SQLException, ClassNotFoundException {
        // Logic to approve an order:
        // 1. Update rental_orders: set status='approved', approval_status='approved', assigned_admin_id=adminId, truck_id=truckId
        // 2. Update trucks: set status='rented' for the assigned truckId (or 'reserved_for_rental')

        Connection conn = null;
        try {
            conn = DbConfig.getDbConnection();
            conn.setAutoCommit(false); // Start transaction

            // Step 1: Update rental_orders table
            String updateOrderSql = "UPDATE rental_orders SET status = ?, approval_status = ?, " +
                                    "assigned_admin_id = ?, truck_id = ? " + // truck_id is the assigned truck
                                    "WHERE order_id = ? AND status = 'pending_approval'"; // Ensure it's still pending

            try (PreparedStatement psOrder = conn.prepareStatement(updateOrderSql)) {
                psOrder.setString(1, "approved"); // Or "confirmed"
                psOrder.setString(2, "approved");
                psOrder.setInt(3, adminId);
                psOrder.setInt(4, truckId);
                psOrder.setInt(5, orderId);

                int orderRowsAffected = psOrder.executeUpdate();
                if (orderRowsAffected == 0) {
                    throw new SQLException("Order approval failed or order was not in pending_approval status. No rows updated in rental_orders.");
                }
            }

            // Step 2: Update trucks table status
            String updateTruckSql = "UPDATE trucks SET status = ? WHERE truck_id = ? AND status = 'available'"; // Ensure truck is available

            try (PreparedStatement psTruck = conn.prepareStatement(updateTruckSql)) {
                psTruck.setString(1, "rented"); // Or a more specific status like 'reserved_for_rental' then 'rented'
                psTruck.setInt(2, truckId);

                int truckRowsAffected = psTruck.executeUpdate();
                if (truckRowsAffected == 0) {
                    // This could happen if the truck chosen by admin was no longer 'available'
                    // Or if an invalid truckId was provided.
                    throw new SQLException("Failed to update truck status. Truck might not be available or invalid truckId.");
                }
            }

            conn.commit(); // Commit transaction

        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    System.err.println("Error during rollback: " + ex.getMessage());
                    e.addSuppressed(ex); // Add rollback error to original error
                }
            }
            System.err.println("Error approving rental order: " + e.getMessage());
            throw e; // Re-throw to be caught by controller
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error closing connection: " + ex.getMessage());
                }
            }
        }
    }
}