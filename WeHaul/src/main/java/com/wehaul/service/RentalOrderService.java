package com.wehaul.service;

import com.wehaul.config.DbConfig;
import com.wehaul.model.RentalOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RentalOrderService {

    // Method to create a rental order request (truck_id will now be populated)
    public void createRentalOrder(RentalOrder order) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO rental_orders (customer_id, truck_id, pickup_date, return_date, " +
                     "pickup_location, return_location, nepali_pickup_location, nepali_return_location, " +
                     "status, approval_status, assigned_admin_id, total_cost, created_at) " + // No assigned_truck_id as separate, truck_id is the assigned one
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // Adjusted placeholder count

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, order.getCustomerId());

            // truck_id should now have a value if findAvailableTruckForBooking was successful
            if (order.getAssignedTruckId() != null) { // Using assignedTruckId from model for the truck_id column
                ps.setInt(paramIndex++, order.getAssignedTruckId());
            } else {
                // This case should ideally not be reached if a truck is always assigned
                throw new SQLException("Truck ID cannot be null when creating a confirmed rental order.");
            }

            ps.setTimestamp(paramIndex++, order.getPickupDate());
            ps.setTimestamp(paramIndex++, order.getReturnDate());
            ps.setString(paramIndex++, order.getPickupLocation());
            ps.setString(paramIndex++, order.getReturnLocation());
            ps.setString(paramIndex++, order.getNepaliPickupLocation());
            ps.setString(paramIndex++, order.getNepaliReturnLocation());
            ps.setString(paramIndex++, order.getStatus()); // e.g., "confirmed"
            ps.setString(paramIndex++, order.getApprovalStatus()); // e.g., "approved" (if auto-approved)

            if (order.getAssignedAdminId() != null) { // Could be system/auto-assigned ID
                ps.setInt(paramIndex++, order.getAssignedAdminId());
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

    /**
     * Finds a specific available truck_id of the requested type for the given dates.
     * This is a more complex inventory check.
     *
     * @param requestedTruckTypeId The ID of the truck type.
     * @param pickupDate The desired pickup timestamp.
     * @param estimatedReturnDate The desired return timestamp.
     * @return An Integer representing the truck_id if one is found, otherwise null.
     * @throws SQLException If a database access error occurs.
     * @throws ClassNotFoundException If the DB driver is not found.
     */
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
                                       "WHERE ro.truck_id = ? " + // Specific truck
                                       "AND ro.status IN (?, ?, ?) " + // Confirmed, Approved, In-Progress
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
                        // This truck has no conflicting bookings, it's available!
                        // **RESERVATION/LOCKING MECHANISM NEEDED HERE FOR ROBUSTNESS**
                        // For simplicity, we assume immediate assignment.
                        // In a high-concurrency system, you'd update truck status to 'reserved'
                        // within a transaction before returning the ID.
                        System.out.println("Found available truck ID: " + truckId + " for type: " + requestedTruckTypeId);
                        return truckId;
                    }
                }
                psCheckConflict.clearParameters(); // Clear parameters for next iteration
            }
        } catch (SQLException e) {
            System.err.println("Error finding available truck: " + e.getMessage());
            throw e;
        } catch (ClassNotFoundException e) {
             System.err.println("Database driver class not found: " + e.getMessage());
             throw new SQLException("Database driver error.", e);
        }

        System.out.println("No specific truck of type " + requestedTruckTypeId + " is available for the selected period.");
        return null; // No available truck found
    }

    // Helper: Get IDs of all operational trucks for a given type_id
    private List<Integer> getOperationalTruckIdsOfType(int truckTypeId) throws SQLException, ClassNotFoundException {
        List<Integer> truckIds = new ArrayList<>();
        // Additionally, only consider trucks that are currently 'available' if your workflow implies this.
        // If a truck is 'rented' its existing rental_order would cause a conflict.
        // If it's 'maintenance', it shouldn't be bookable.
        String sql = "SELECT truck_id FROM trucks WHERE type_id = ? AND status = 'available'";
        // Or, if 'maintenance' is short-term and doesn't block future bookings far out:
        // String sql = "SELECT truck_id FROM trucks WHERE type_id = ? AND status != 'decommissioned'";


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

    /**
     * Optional: Method to update truck status (e.g., to 'rented' or 'reserved')
     * This should be part of a transaction with createRentalOrder.
     */
    public boolean updateTruckStatus(int truckId, String newStatus, Connection conn) throws SQLException {
        String sql = "UPDATE trucks SET status = ? WHERE truck_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, truckId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
        // Connection is managed externally if passed in (for transactions)
    }
}