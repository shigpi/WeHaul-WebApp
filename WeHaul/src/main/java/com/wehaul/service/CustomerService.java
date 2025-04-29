package com.wehaul.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}



