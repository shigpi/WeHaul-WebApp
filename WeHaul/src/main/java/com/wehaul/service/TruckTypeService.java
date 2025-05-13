package com.wehaul.service;

import com.wehaul.config.DbConfig;
import com.wehaul.model.TruckType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TruckTypeService {

    public List<TruckType> getAllTruckTypes() throws SQLException, ClassNotFoundException {
        List<TruckType> truckTypes = new ArrayList<>();
        String sql = "SELECT type_id, name, description, daily_rate, capacity FROM truck_types ORDER BY name";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TruckType type = mapResultSetToTruckType(rs);
                truckTypes.add(type);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all truck types: " + e.getMessage());
            throw e;
        }
        return truckTypes;
    }

    public TruckType getTruckTypeById(int typeId) throws SQLException, ClassNotFoundException {
        TruckType truckType = null;
        String sql = "SELECT type_id, name, description, daily_rate, capacity FROM truck_types WHERE type_id = ?";

        try (Connection conn = DbConfig.getDbConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    truckType = mapResultSetToTruckType(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching truck type by ID " + typeId + ": " + e.getMessage());
            throw e;
        }
        return truckType;
    }

    private TruckType mapResultSetToTruckType(ResultSet rs) throws SQLException {
        TruckType type = new TruckType();
        type.setTypeId(rs.getInt("type_id"));
        type.setName(rs.getString("name"));
        type.setDescription(rs.getString("description"));
        type.setDailyRate(rs.getBigDecimal("daily_rate"));
        type.setCapacity(rs.getString("capacity"));
        return type;
    }
}
