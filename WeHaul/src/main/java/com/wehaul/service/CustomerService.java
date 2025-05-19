package com.wehaul.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.wehaul.model.Customer;
import com.wehaul.config.DbConfig;

public class CustomerService {

    public Customer getCustomerByEmail(String email) throws SQLException {
        Customer customer = null;
        String sql = "SELECT * FROM customers WHERE email = ?";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setFirstName(rs.getString("first_name"));
                    customer.setLastName(rs.getString("last_name"));
                    customer.setEmail(rs.getString("email"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setAddress(rs.getString("address"));
                    customer.setCity(rs.getString("city"));
                    customer.setState(rs.getString("state"));
                    customer.setPasswordHash(rs.getString("password_hash"));
                    customer.setAdmin(rs.getBoolean("is_admin"));
                }
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database driver not found", e);
        }

        return customer;
    }

    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (first_name, last_name, email, phone, address, city, state, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getCity());
            stmt.setString(7, customer.getState());
            stmt.setString(8, customer.getPasswordHash());

            stmt.executeUpdate();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database driver not found", e);
        }
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone = ?, address = ?, city = ?, state = ? WHERE customer_id = ?";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getCity());
            stmt.setString(7, customer.getState());
            stmt.setInt(8, customer.getCustomerId());

            stmt.executeUpdate();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database driver not found", e);
        }
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
                // Note: Password hash is usually not fetched for general listings
                customers.add(customer);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Error fetching all customers: " + e.getMessage());
            throw e;
        }
        return customers;
    }
    
    public Customer getCustomerById(int customerId) throws SQLException, ClassNotFoundException {
        Customer customer = null;
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customer = new Customer();
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
                    // Avoid loading password hash unless specifically needed for auth
                }
            }
        }
        return customer;
    }
}



