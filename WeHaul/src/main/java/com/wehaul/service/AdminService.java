package com.wehaul.service;

import com.wehaul.config.DbConfig;
import com.wehaul.model.Customer;
import com.wehaul.model.DashboardStatsModel;
import com.wehaul.model.RentalOrder;
import com.wehaul.model.Truck;
import com.wehaul.model.TruckType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AdminService {

    public DashboardStatsModel getCurrentDashboardStats() throws SQLException, ClassNotFoundException {
        DashboardStatsModel stats = new DashboardStatsModel();
        
        String sqlTotalCustomers = "SELECT COUNT(*) FROM customers WHERE is_admin = false";
        String sqlAvailableTrucks = "SELECT COUNT(*) FROM trucks WHERE status = 'available'";
        String sqlActiveRentals = "SELECT COUNT(*) FROM rental_orders WHERE status IN ('approved', 'in_progress', 'confirmed')";
        String sqlPendingOrders = "SELECT COUNT(*) FROM rental_orders WHERE approval_status = 'pending' OR status = 'pending_approval'";

        Connection conn = null;
        try {
            conn = DbConfig.getDbConnection();
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlTotalCustomers)) {
                if (rs.next()) stats.setTotalCustomers(rs.getInt(1));
            }
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlAvailableTrucks)) {
                if (rs.next()) stats.setAvailableTrucks(rs.getInt(1));
            }
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlActiveRentals)) {
                if (rs.next()) stats.setActiveRentals(rs.getInt(1));
            }
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlPendingOrders)) {
                if (rs.next()) stats.setPendingOrders(rs.getInt(1));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("DB error fetching dashboard stats: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { System.err.println("Error closing conn (dashboard): " + e.getMessage()); }
            }
        }
        return stats;
    }

    public List<Truck> getAllTrucks() throws SQLException, ClassNotFoundException {
        List<Truck> trucks = new ArrayList<>();
        String sql = "SELECT t.truck_id, t.type_id, tt.name AS type_name, t.license_plate, t.make, t.model, " +
                     "t.year, t.mileage, t.last_maintenance_date, t.status, t.current_location " +
                     "FROM trucks t " +
                     "LEFT JOIN truck_types tt ON t.type_id = tt.type_id " +
                     "ORDER BY t.truck_id ASC";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
                trucks.add(truck);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching all trucks: " + e.getMessage());
            throw e;
        }
        return trucks;
    }

    public List<Customer> getAllCustomers() throws SQLException, ClassNotFoundException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, first_name, last_name, email, phone, address, city, state, is_admin, created_at, updated_at " +
                     "FROM customers ORDER BY last_name, first_name";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
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
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching all customers: " + e.getMessage());
            throw e;
        }
        return customers;
    }

    public List<RentalOrder> getAllOrders() throws SQLException, ClassNotFoundException {
        List<RentalOrder> orders = new ArrayList<>();
        String sql = "SELECT ro.order_id, ro.customer_id, ro.assigned_truck_id AS order_assigned_truck_id, " +
                "ro.pickup_date, ro.return_date, ro.pickup_location, ro.return_location, " +
                "ro.nepali_pickup_location, ro.nepali_return_location, ro.status, ro.approval_status, " +
                "ro.assigned_admin_id, ro.total_cost, ro.created_at, " +
                "c.first_name AS customer_first_name, c.last_name AS customer_last_name, " +
                "t_assigned.license_plate AS assigned_truck_license_plate, " +
                "tt.name AS truck_type_name, tt.type_id AS actual_truck_type_id " +
                "FROM rental_orders ro " +
                "LEFT JOIN customers c ON ro.customer_id = c.customer_id " +
                "LEFT JOIN trucks t_assigned ON ro.assigned_truck_id = t_assigned.truck_id " +
                "LEFT JOIN truck_types tt ON t_assigned.type_id = tt.type_id " +
                "ORDER BY ro.created_at DESC";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RentalOrder order = new RentalOrder();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("customer_id"));
                order.setAssignedTruckId(rs.getObject("order_assigned_truck_id", Integer.class));
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
                    assignedTruck.setTypeId(rs.getInt("actual_truck_type_id"));
                    order.setAssignedTruckInfo(assignedTruck);

                    TruckType truckType = new TruckType();
                    truckType.setTypeId(rs.getInt("actual_truck_type_id"));
                    truckType.setName(rs.getString("truck_type_name"));
                    order.setTruckTypeInfo(truckType);
                }
                orders.add(order);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
            throw e;
        }
        return orders;
    }

    public void approveRentalOrder(int orderId, int adminId, int truckIdToAssign) throws SQLException, ClassNotFoundException {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psTruck = null;
        boolean originalAutoCommitState = true;
        
        String newMainOrderStatusAfterAdminApproval = "in_progress"; 
        String newApprovalStatusAfterAdminApproval = "approved"; 

        try {
            conn = DbConfig.getDbConnection();
            originalAutoCommitState = conn.getAutoCommit();
            if (originalAutoCommitState) {
                conn.setAutoCommit(false);
            }

            Truck truck = getTruckById(truckIdToAssign);

            String updateOrderSql = "UPDATE rental_orders SET status = ?, approval_status = ?, " +
                                    "assigned_admin_id = ?, assigned_truck_id = ? " +
                                    "WHERE order_id = ? AND (approval_status = 'pending' OR status = 'pending_approval')";

            psOrder = conn.prepareStatement(updateOrderSql);
            int paramIndexOrder = 1;
            psOrder.setString(paramIndexOrder++, newMainOrderStatusAfterAdminApproval); 
            psOrder.setString(paramIndexOrder++, newApprovalStatusAfterAdminApproval); 
            psOrder.setInt(paramIndexOrder++, adminId);                     
            psOrder.setInt(paramIndexOrder++, truckIdToAssign);             
            psOrder.setInt(paramIndexOrder++, orderId);                     

            System.out.println("ADMIN_SERVICE (approveOrder): SQL: " + updateOrderSql);
            System.out.println("ADMIN_SERVICE (approveOrder): Params: status=" + newMainOrderStatusAfterAdminApproval +
                               ", approval_status=" + newApprovalStatusAfterAdminApproval + ", adminId=" + adminId +
                               ", truckId=" + truckIdToAssign + ", orderId=" + orderId);

            int orderRowsAffected = psOrder.executeUpdate();
            System.out.println("ADMIN_SERVICE (approveOrder): Rows affected for order update: " + orderRowsAffected);
            if (orderRowsAffected == 0) {
                throw new SQLException("Order approval failed. Order ID: " + orderId + " not found, not in a pending state, or no changes needed.");
            }

            String updateTruckSql = "UPDATE trucks SET status = 'rented' WHERE truck_id = ?";
            psTruck = conn.prepareStatement(updateTruckSql);
            psTruck.setInt(1, truckIdToAssign);
            int truckRowsAffected = psTruck.executeUpdate();
            System.out.println("ADMIN_SERVICE (approveOrder): Rows affected for truck update: " + truckRowsAffected);
            if (truckRowsAffected == 0 && "available".equalsIgnoreCase(truck.getStatus())) { // truck variable from getTruckById() call
                System.err.println("Warning: Truck status for " + truckIdToAssign + " was expected to change to 'rented' but no rows were affected.");
            }

            if (originalAutoCommitState) {
                conn.commit();
                System.out.println("ADMIN_SERVICE (approveOrder): Transaction committed.");
            }

        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null && originalAutoCommitState) {
                try { 
                    conn.rollback(); 
                    System.err.println("ADMIN_SERVICE (approveOrder): Transaction rolled back due to error.");
                } catch (SQLException ex) { 
                    System.err.println("ADMIN_SERVICE (approveOrder): Error during rollback: " + ex.getMessage());
                    e.addSuppressed(ex); 
                }
            }
            System.err.println("Error approving rental order " + orderId + ": " + e.getMessage());
            throw e;
        } finally {
            if (psOrder != null) try { psOrder.close(); } catch (SQLException e) { /* ignored */ }
            if (psTruck != null) try { psTruck.close(); } catch (SQLException e) { /* ignored */ }
            if (conn != null) {
                try {
                    if (originalAutoCommitState && !conn.isClosed() && !conn.getAutoCommit()) { 
                        conn.setAutoCommit(true); 
                    }
                    if (!conn.isClosed()) { 
                        conn.close(); 
                    }
                } catch (SQLException ex) { /* ignored */ }
            }
        }
    }

    public RentalOrder getOrderById(int orderId) throws SQLException, ClassNotFoundException {
        RentalOrder order = null;
        String sql = "SELECT ro.order_id, ro.customer_id, ro.pickup_date, ro.return_date, " +
                "ro.pickup_location, ro.return_location, ro.nepali_pickup_location, ro.nepali_return_location, " +
                "ro.status, ro.approval_status, ro.assigned_admin_id, ro.assigned_truck_id, " +
                "ro.total_cost, ro.created_at, " + 
                "c.first_name AS customer_first_name, c.last_name AS customer_last_name, c.email AS customer_email, " +
                "t_assigned.license_plate AS assigned_truck_license_plate, t_assigned.make AS assigned_truck_make, " +
                "t_assigned.model AS assigned_truck_model, t_assigned.type_id AS actual_truck_type_id, " +
                "tt_assigned.name AS assigned_truck_type_name, tt_assigned.daily_rate AS assigned_truck_type_daily_rate, " +
                "tt_assigned.capacity AS assigned_truck_type_capacity " +
                "FROM rental_orders ro " +
                "JOIN customers c ON ro.customer_id = c.customer_id " +
                "LEFT JOIN trucks t_assigned ON ro.assigned_truck_id = t_assigned.truck_id " +
                "LEFT JOIN truck_types tt_assigned ON t_assigned.type_id = tt_assigned.type_id " +
                "WHERE ro.order_id = ?";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new RentalOrder();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getObject("customer_id", Integer.class));
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
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setFirstName(rs.getString("customer_first_name"));
                    customer.setLastName(rs.getString("customer_last_name"));
                    customer.setEmail(rs.getString("customer_email"));
                    order.setCustomer(customer);

                    if (order.getAssignedTruckId() != null) {
                        Truck assignedTruck = new Truck();
                        assignedTruck.setTruckId(order.getAssignedTruckId());
                        assignedTruck.setLicensePlate(rs.getString("assigned_truck_license_plate"));
                        assignedTruck.setMake(rs.getString("assigned_truck_make"));
                        assignedTruck.setModel(rs.getString("assigned_truck_model"));
                        assignedTruck.setTypeId(rs.getInt("actual_truck_type_id"));
                        order.setAssignedTruckInfo(assignedTruck);

                        TruckType truckType = new TruckType();
                        truckType.setTypeId(rs.getInt("actual_truck_type_id"));
                        truckType.setName(rs.getString("assigned_truck_type_name"));
                        if (rs.getBigDecimal("assigned_truck_type_daily_rate") != null) {
                           truckType.setDailyRate(rs.getBigDecimal("assigned_truck_type_daily_rate"));
                        }
                        truckType.setCapacity(rs.getString("assigned_truck_type_capacity"));
                        order.setTruckTypeInfo(truckType);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching order by ID " + orderId + ": " + e.getMessage());
            throw e;
        }
        return order;
    }

    public void updateRentalOrder(RentalOrder order) throws SQLException, ClassNotFoundException {
    	String sql = "UPDATE rental_orders SET " +
                "customer_id = ?, " +            
                "truck_id = ?, " +               
                "pickup_date = ?, " +            
                "return_date = ?, " +            
                "pickup_location = ?, " +        
                "return_location = ?, " +        
                "nepali_pickup_location = ?, " + 
                "nepali_return_location = ?, " + 
                "status = ?, " +                 
                "approval_status = ?, " +        
                "assigned_admin_id = ?, " +      
                "assigned_truck_id = ?, " +      
                "total_cost = ? " +              
                "WHERE order_id = ?"; 
        Connection conn = null;
        PreparedStatement ps = null;
        boolean originalAutoCommitState = true;

        try {
            conn = DbConfig.getDbConnection();
            originalAutoCommitState = conn.getAutoCommit();
            if (originalAutoCommitState) {
                conn.setAutoCommit(false);
            }

            ps = conn.prepareStatement(sql);

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
            ps.setString(paramIndex++, order.getStatus());
            ps.setString(paramIndex++, order.getApprovalStatus());

            if (order.getAssignedAdminId() != null) ps.setInt(paramIndex++, order.getAssignedAdminId());
            else ps.setNull(paramIndex++, Types.INTEGER);

            // Using order.getAssignedTruckId() for 'assigned_truck_id' DB column again
            if (order.getAssignedTruckId() != null) {
                ps.setInt(paramIndex++, order.getAssignedTruckId());
            } else {
                ps.setNull(paramIndex++, Types.INTEGER);
            }

            ps.setBigDecimal(paramIndex++, order.getTotalCost());
            ps.setInt(paramIndex++, order.getOrderId());

            int affectedRows = ps.executeUpdate();
             System.out.println("ADMIN_SERVICE: Rows affected by rental_orders update: " + affectedRows); // Debug line

            if (affectedRows == 0) {
                 if (originalAutoCommitState) conn.rollback();
                throw new SQLException("Updating rental order failed, no rows affected. Order ID: " + order.getOrderId());
            }

            if (originalAutoCommitState) {
                conn.commit();
            }

        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null && originalAutoCommitState) {
                try { conn.rollback(); } catch (SQLException ex) { e.addSuppressed(ex); }
            }
            System.err.println("Error updating rental order " + order.getOrderId() + ": " + e.getMessage());
            throw e;
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException e) { /* Ignored */ }
            if (conn != null) {
                try {
                    if (originalAutoCommitState && !conn.isClosed() && !conn.getAutoCommit()) {
                         conn.setAutoCommit(true);
                    }
                    if (!conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) { /* Ignored */ }
            }
        }
    }

    public Truck getTruckById(int truckId) throws SQLException, ClassNotFoundException {
        Truck truck = null;
        String sql = "SELECT t.truck_id, t.type_id, tt.name AS type_name, " +
                     "t.license_plate, t.make, t.model, t.year, t.mileage, " +
                     "t.last_maintenance_date, t.status, t.current_location " +
                     "FROM trucks t " +
                     "LEFT JOIN truck_types tt ON t.type_id = tt.type_id " +
                     "WHERE t.truck_id = ?";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, truckId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    truck = new Truck();
                    truck.setTruckId(rs.getInt("truck_id"));
                    truck.setTypeId(rs.getInt("type_id"));
                    truck.setLicensePlate(rs.getString("license_plate"));
                    truck.setMake(rs.getString("make"));
                    truck.setModel(rs.getString("model"));
                    truck.setYear(rs.getInt("year"));
                    truck.setMileage(rs.getInt("mileage"));
                    truck.setLastMaintenanceDate(rs.getDate("last_maintenance_date"));
                    truck.setStatus(rs.getString("status"));
                    truck.setCurrentLocation(rs.getString("current_location"));
                    if (rs.getString("type_name") != null) {
                        truck.setTypeName(rs.getString("type_name"));
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching truck by ID " + truckId + ": " + e.getMessage());
            throw e;
        }
        return truck;
    }

    public void updateTruckStatus(int truckId, String newStatus) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE trucks SET status = ? WHERE truck_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        boolean originalAutoCommitState = true;

        try {
            conn = DbConfig.getDbConnection();
            originalAutoCommitState = conn.getAutoCommit();
             if(originalAutoCommitState){
                conn.setAutoCommit(false);
             }

            ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, truckId);
            int affectedRows = ps.executeUpdate();
             System.out.println("ADMIN_SERVICE: updateTruckStatus - Rows affected: " + affectedRows); // Debug line

            if(originalAutoCommitState){
                conn.commit();
            }

        } catch (SQLException | ClassNotFoundException e) {
            if(conn != null && originalAutoCommitState){
                try{ conn.rollback(); } catch (SQLException ex){ e.addSuppressed(ex); }
            }
            System.err.println("Error updating truck status for truck ID " + truckId + ": " + e.getMessage());
            throw e;
        } finally {
             if (ps != null) try { ps.close(); } catch (SQLException e) { /* Ignored */ }
             if (conn != null) {
                try {
                    if (originalAutoCommitState && !conn.isClosed() && !conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                     if(!conn.isClosed()){ conn.close();}
                } catch (SQLException e) { /* Ignored */ }
            }
        }
    }
    
    public boolean deleteTruckById(int truckId) throws SQLException, ClassNotFoundException {
        String sqlCheckOrders = "SELECT COUNT(*) FROM rental_orders WHERE assigned_truck_id = ?";
        String sqlDeleteTruck = "DELETE FROM trucks WHERE truck_id = ?";
        
        Connection conn = null;
        PreparedStatement psCheck = null;
        PreparedStatement psDelete = null;
        ResultSet rsCheck = null;
        boolean originalAutoCommitState = true;
        int affectedRows = 0;

        try {
            conn = DbConfig.getDbConnection();
            originalAutoCommitState = conn.getAutoCommit();
            if (originalAutoCommitState) {
                conn.setAutoCommit(false); // Start transaction
            }

            // Check if the truck is assigned to any orders
            psCheck = conn.prepareStatement(sqlCheckOrders);
            psCheck.setInt(1, truckId);
            rsCheck = psCheck.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                if (originalAutoCommitState) conn.rollback(); // Rollback before throwing
                throw new SQLException("Cannot delete truck ID " + truckId + ". It is currently assigned to one or more rental orders. Please unassign it first or ensure orders are completed/cancelled.");
                
            }
            rsCheck.close();
            psCheck.close();


            // If not assigned, proceed with deletion
            psDelete = conn.prepareStatement(sqlDeleteTruck);
            psDelete.setInt(1, truckId);
            affectedRows = psDelete.executeUpdate();

            if (originalAutoCommitState) {
                conn.commit(); // Commit transaction
            }
            System.out.println("ADMIN_SERVICE: Deleted truck ID " + truckId + ". Rows affected: " + affectedRows);

        } catch (SQLException | ClassNotFoundException e) {
            if (conn != null && originalAutoCommitState) {
                try { conn.rollback(); } catch (SQLException ex) { e.addSuppressed(ex); }
            }
            System.err.println("Error deleting truck ID " + truckId + ": " + e.getMessage());
            throw e;
        } finally {
            if (rsCheck != null) try { rsCheck.close(); } catch (SQLException e) { /* ignored */ }
            if (psCheck != null) try { psCheck.close(); } catch (SQLException e) { /* ignored */ }
            if (psDelete != null) try { psDelete.close(); } catch (SQLException e) { /* ignored */ }
            if (conn != null) {
                try {
                    if (originalAutoCommitState && !conn.isClosed() && !conn.getAutoCommit()) {
                        conn.setAutoCommit(true);
                    }
                    if (!conn.isClosed()) { conn.close(); }
                } catch (SQLException e) { /* ignored */ }
            }
        }
        return affectedRows > 0;
    }
}