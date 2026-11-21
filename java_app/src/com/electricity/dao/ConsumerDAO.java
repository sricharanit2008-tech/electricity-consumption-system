package com.electricity.dao;

import com.electricity.model.Consumer;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Data Access Object for Consumer management.
 * Functional Requirement 1: Maintain consumer details (ID, name, address, connection type).
 */
public class ConsumerDAO {
    // In-memory fallback cache
    private static final Map<String, Consumer> consumerStore = new LinkedHashMap<>();

    static {
        // Pre-populate with default consumers for standalone runs
        initDefaultConsumers();
    }

    private static void initDefaultConsumers() {
        addSample(new Consumer("C101", "Aarav Sharma", "14 Maple Residency, Green Park", "9876543210", "Domestic", "MTR-DOM-101", LocalDate.of(2022, 1, 15), "Active"));
        addSample(new Consumer("C102", "Priya Patel", "22 Sunset Boulevard, Sector 4", "9812345678", "Domestic", "MTR-DOM-102", LocalDate.of(2022, 3, 20), "Active"));
        addSample(new Consumer("C103", "Rajesh Verma", "88 Hill Road, Civil Lines", "9723456789", "Domestic", "MTR-DOM-103", LocalDate.of(2021, 11, 10), "Active"));
        addSample(new Consumer("C104", "Ananya Gupta", "104 Lakeview Apartments", "9934567890", "Domestic", "MTR-DOM-104", LocalDate.of(2023, 5, 12), "Active"));
        addSample(new Consumer("C105", "Vikram Singh", "45 Royal Enclave", "9845678901", "Domestic", "MTR-DOM-105", LocalDate.of(2020, 8, 1), "Active"));

        addSample(new Consumer("C201", "Apex Tech Solutions Hub", "IT Tech Park, Block C", "9856789012", "Commercial", "MTR-COM-201", LocalDate.of(2021, 2, 14), "Active"));
        addSample(new Consumer("C202", "Metro Central Supermarket", "Market Square, Main Road", "9867890123", "Commercial", "MTR-COM-202", LocalDate.of(2022, 6, 18), "Active"));
        addSample(new Consumer("C203", "Grand Cafe & Bakers", "Corner Street, Downtown", "9878901234", "Commercial", "MTR-COM-203", LocalDate.of(2023, 1, 5), "Active"));

        addSample(new Consumer("C301", "Falcon Heavy Steels Ltd", "Industrial Area, Phase 1", "9889012345", "Industrial", "MTR-IND-301", LocalDate.of(2019, 4, 10), "Active"));
        addSample(new Consumer("C302", "Sunrise Textile Mills", "MIDC Zone, Plot 5", "9890123456", "Industrial", "MTR-IND-302", LocalDate.of(2020, 9, 22), "Active"));

        addSample(new Consumer("C401", "Kisan Agro Cooperative", "Village Ramnagar, Farm 12", "9901234567", "Agricultural", "MTR-AGR-401", LocalDate.of(2021, 7, 30), "Active"));
        addSample(new Consumer("C402", "Greenfield Dairy Farms", "National Highway 48", "9912345678", "Agricultural", "MTR-AGR-402", LocalDate.of(2022, 10, 15), "Active"));
    }

    private static void addSample(Consumer c) {
        consumerStore.put(c.getConsumerId().toUpperCase(), c);
    }

    public boolean addConsumer(Consumer consumer) {
        consumerStore.put(consumer.getConsumerId().toUpperCase(), consumer);

        if (DBConnection.isDriverAvailable()) {
            String sql = "INSERT INTO consumers (consumer_id, name, address, phone, connection_type, meter_number, connection_date, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, consumer.getConsumerId());
                stmt.setString(2, consumer.getName());
                stmt.setString(3, consumer.getAddress());
                stmt.setString(4, consumer.getPhone());
                stmt.setString(5, consumer.getConnectionType());
                stmt.setString(6, consumer.getMeterNumber());
                stmt.setDate(7, java.sql.Date.valueOf(consumer.getConnectionDate()));
                stmt.setString(8, consumer.getStatus());
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.err.println("[ConsumerDAO] DB Insert Error: " + e.getMessage());
            }
        }
        return true;
    }

    /**
     * Functional Requirement 1: Update existing consumer details.
     */
    public boolean updateConsumer(Consumer consumer) {
        if (consumer == null || consumer.getConsumerId() == null) return false;
        consumerStore.put(consumer.getConsumerId().toUpperCase(), consumer);

        if (DBConnection.isDriverAvailable()) {
            String sql = "UPDATE consumers SET name = ?, address = ?, phone = ?, connection_type = ?, meter_number = ?, status = ? WHERE UPPER(consumer_id) = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, consumer.getName());
                stmt.setString(2, consumer.getAddress());
                stmt.setString(3, consumer.getPhone());
                stmt.setString(4, consumer.getConnectionType());
                stmt.setString(5, consumer.getMeterNumber());
                stmt.setString(6, consumer.getStatus());
                stmt.setString(7, consumer.getConsumerId().toUpperCase());
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.err.println("[ConsumerDAO] DB Update Error: " + e.getMessage());
            }
        }
        return true;
    }

    public Consumer getConsumerById(String consumerId) {
        if (DBConnection.isDriverAvailable()) {
            String sql = "SELECT * FROM consumers WHERE UPPER(consumer_id) = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, consumerId.toUpperCase());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return mapRow(rs);
                }
            } catch (SQLException e) {
                System.err.println("[ConsumerDAO] DB Query Error: " + e.getMessage());
            }
        }
        return consumerStore.get(consumerId.toUpperCase());
    }

    public List<Consumer> getAllConsumers() {
        if (DBConnection.isDriverAvailable()) {
            List<Consumer> list = new ArrayList<>();
            String sql = "SELECT * FROM consumers ORDER BY connection_type, consumer_id";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                if (!list.isEmpty()) {
                    return list;
                }
            } catch (SQLException e) {
                System.err.println("[ConsumerDAO] DB Query Error: " + e.getMessage());
            }
        }
        return new ArrayList<>(consumerStore.values());
    }

    private Consumer mapRow(ResultSet rs) throws SQLException {
        Consumer c = new Consumer();
        c.setConsumerId(rs.getString("consumer_id"));
        c.setName(rs.getString("name"));
        c.setAddress(rs.getString("address"));
        c.setPhone(rs.getString("phone"));
        c.setConnectionType(rs.getString("connection_type"));
        c.setMeterNumber(rs.getString("meter_number"));
        java.sql.Date dt = rs.getDate("connection_date");
        c.setConnectionDate(dt != null ? dt.toLocalDate() : LocalDate.now());
        c.setStatus(rs.getString("status"));
        return c;
    }
}
