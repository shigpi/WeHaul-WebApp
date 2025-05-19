package com.wehaul.service;

import com.wehaul.config.DbConfig;
import com.wehaul.model.Customer;
import com.wehaul.model.RentalOrder;
import com.wehaul.model.Truck;
import com.wehaul.model.TruckType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class RentalOrderService {

    public void createRentalOrder(RentalOrder order) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO rental_orders (customer_id, truck_id, pickup_date, return_date, " +
                     "pickup_location, return_location, nepali_pickup_location, nepali_return_location, " +
                     "status, approval_status, assigned_admin_id, assigned_truck_id, total_cost, created_at) " + 
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, order.getCustomerId()); 
            
            if (order.getAssignedTruckId() != null) {
                ps.setInt(paramIndex++, order.getAssignedTruckId());
            } else {
                ps.setNull(paramIndex++, Types.INTEGER); 
            }
            

            ps.setTimestamp(paramIndex++, order.getPickupDate());
            ps.setTimestamp(paramIndex++, order.getReturnDate());
            ps.setString(paramIndex++, order.getPickupLocation());
            ps.setString(paramIndex++, order.getReturnLocation());
            ps.setString(paramIndex++, order.getNepaliPickupLocation());
            ps.setString(paramIndex++, order.getNepaliReturnLocation());
            ps.setString(paramIndex++, "pending"); 
            ps.setString(paramIndex++, order.getApprovalStatus()); 

            if (order.getAssignedAdminId() != null) { 
                ps.setInt(paramIndex++, order.getAssignedAdminId());
            } else {
                ps.setNull(paramIndex++, Types.INTEGER);
            }
            
            if (order.getAssignedTruckId() != null) { 
                ps.setInt(paramIndex++, order.getAssignedTruckId());
            } else {
                ps.setNull(paramIndex++, Types.INTEGER);
            }

            ps.setBigDecimal(paramIndex++, order.getTotalCost());
            ps.setTimestamp(paramIndex++, order.getCreatedAt());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating rental order failed, no rows affected.");
            }

        } catch (SQLException e) {
            System.err.println("Error creating rental order: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
             System.err.println("Database driver class not found: " + e.getMessage());
             throw new SQLException("Database driver error.", e);
        }
    }

    public Integer findAndReserveAvailableTruckForBooking(int requestedTruckTypeId, Timestamp pickupDate, Timestamp estimatedReturnDate)
            throws SQLException, ClassNotFoundException {

        // Step 1: Get all truck_ids of the requested type that are generally operational
        List<Integer> operationalTruckIdsOfType = getOperationalTruckIdsOfType(requestedTruckTypeId);
        if (operationalTruckIdsOfType.isEmpty()) {
            System.err.println("No operational trucks found for type ID: " + requestedTruckTypeId);
            return null; // No trucks of this type to even consider
        }

        // Step 2: For each operational truck, check if it's free during the requested period
        String sqlCheckTruckConflict = "SELECT COUNT(ro.order_id) FROM rental_orders ro " +
                                       "WHERE ro.truck_id = ? " +
                                       "AND ro.status IN (?, ?, ?) " + 
                                       "AND ro.pickup_date < ? " +
                                       "AND ro.return_date > ?";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement psCheckConflict = conn.prepareStatement(sqlCheckTruckConflict)) {

            for (Integer truckId : operationalTruckIdsOfType) {
                psCheckConflict.setInt(1, truckId);
                psCheckConflict.setString(2, "confirmed");
                psCheckConflict.setString(3, "approved");
                psCheckConflict.setString(4, "in_progress");
                psCheckConflict.setTimestamp(5, estimatedReturnDate);
                psCheckConflict.setTimestamp(6, pickupDate);

                try (ResultSet rs = psCheckConflict.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        System.out.println("Found available truck ID: " + truckId + " for type: " + requestedTruckTypeId);
                        return truckId;
                    }
                }
                psCheckConflict.clearParameters(); 
            }
        } catch (SQLException e) {
            System.err.println("Error finding available truck: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
             System.err.println("Database driver class not found: " + e.getMessage());
             throw new SQLException("Database driver error.", e);
        }

        System.out.println("No specific truck of type " + requestedTruckTypeId + " is available for the selected period.");
        return null; 
    }
    
    private List<Integer> getOperationalTruckIdsOfType(int truckTypeId) throws SQLException, ClassNotFoundException {
        List<Integer> truckIds = new ArrayList<>();
        String sql = "SELECT truck_id FROM trucks WHERE type_id = ? AND status = 'available'";


        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, truckTypeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    truckIds.add(rs.getInt("truck_id"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting operational truck IDs for type " + truckTypeId + ": " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
             System.err.println("Database driver class not found: " + e.getMessage());
             throw new SQLException("Database driver error.", e);
        }
        return truckIds;
    }
    
    public boolean updateTruckStatus(int truckId, String newStatus, Connection conn) throws SQLException {
        String sql = "UPDATE trucks SET status = ? WHERE truck_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, truckId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    public List<RentalOrder> getRentalOrdersByCustomerId(int customerId) throws SQLException, ClassNotFoundException {
        List<RentalOrder> orders = new ArrayList<>();
        String sql = "SELECT ro.order_id, ro.customer_id, " +
                     "ro.truck_id AS requested_truck_type_id_from_db, " +
                     "ro.assigned_truck_id, " +
                     "ro.pickup_date, ro.return_date, ro.pickup_location, ro.return_location, " +
                     "ro.nepali_pickup_location, ro.nepali_return_location, ro.status, ro.approval_status, " +
                     "ro.assigned_admin_id, ro.total_cost, ro.created_at, " +
                     "c.first_name AS customer_first_name, c.last_name AS customer_last_name, " +
                     "t_assigned.license_plate AS assigned_truck_license_plate, " +
                     "t_assigned.type_id AS assigned_truck_db_type_id, " +
                     "assigned_tt.name AS assigned_truck_actual_type_name, " +
                     "assigned_tt.type_id AS assigned_truck_actual_type_id_from_type_table " +
                     "FROM rental_orders ro " +
                     "JOIN customers c ON ro.customer_id = c.customer_id " +
                     "LEFT JOIN trucks t_assigned ON ro.assigned_truck_id = t_assigned.truck_id " +
                     "LEFT JOIN truck_types assigned_tt ON t_assigned.type_id = assigned_tt.type_id " +
                     "WHERE ro.customer_id = ? " +
                     "ORDER BY ro.created_at DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DbConfig.getDbConnection();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerId);
            rs = ps.executeQuery();

            while (rs.next()) {
                RentalOrder order = new RentalOrder();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setTruckId(rs.getObject("requested_truck_type_id_from_db", Integer.class));
                order.setAssignedTruckId(rs.getObject("assigned_truck_id", Integer.class));
                order.setPickupDate(rs.getTimestamp("pickup_date"));
                order.setReturnDate(rs.getTimestamp("return_date"));
                order.setPickupLocation(rs.getString("pickup_location"));
                order.setReturnLocation(rs.getString("return_location"));
                order.setNepaliPickupLocation(rs.getString("nepali_pickup_location"));
                order.setNepaliReturnLocation(rs.getString("nepali_return_location"));
                order.setStatus(rs.getString("status"));
                order.setApprovalStatus(rs.getString("approval_status"));
                order.setAssignedAdminId(rs.getObject("assigned_admin_id", Integer.class));
                order.setTotalCost(rs.getBigDecimal("total_cost"));
                order.setCreatedAt(rs.getTimestamp("created_at"));

                Customer customer = new Customer();
                customer.setCustomerId(order.getCustomerId());
                customer.setFirstName(rs.getString("customer_first_name"));
                customer.setLastName(rs.getString("customer_last_name"));
                order.setCustomer(customer);

                if (order.getAssignedTruckId() != null) {
                    Truck assignedTruck = new Truck();
                    assignedTruck.setTruckId(order.getAssignedTruckId());
                    assignedTruck.setLicensePlate(rs.getString("assigned_truck_license_plate"));
                    assignedTruck.setTypeId(rs.getInt("assigned_truck_db_type_id"));
                    order.setAssignedTruckInfo(assignedTruck);

                    TruckType truckType = new TruckType();
                    truckType.setTypeId(rs.getInt("assigned_truck_actual_type_id_from_type_table"));
                    truckType.setName(rs.getString("assigned_truck_actual_type_name"));
                    order.setTruckTypeInfo(truckType);
                }
                orders.add(order);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching rental orders for customer ID " + customerId + ": " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { /* ignored */ }
            if (ps != null) try { ps.close(); } catch (SQLException e) { /* ignored */ }
            if (conn != null) try { conn.close(); } catch (SQLException e) { /* ignored */ }
        }
        return orders;
    }
    
    
    
}