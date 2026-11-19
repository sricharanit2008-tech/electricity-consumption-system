package com.electricity.dao;

import com.electricity.model.MeterReading;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Data Access Object for Meter Readings.
 * Functional Requirement 2: Record previous and current meter readings for each billing period.
 * Functional Requirement 3: Calculate electricity consumption by subtracting the previous reading from the current reading.
 */
public class MeterReadingDAO {
    private static final List<MeterReading> readingStore = new ArrayList<>();
    private static int nextReadingId = 100;

    static {
        initDefaultReadings();
    }

    private static void initDefaultReadings() {
        // C101 Domestic readings
        addSample(new MeterReading(1, "C101", "2024-01", LocalDate.of(2024, 1, 31), 1200.0, 1340.0, 140.0));
        addSample(new MeterReading(2, "C101", "2024-02", LocalDate.of(2024, 2, 29), 1340.0, 1505.0, 165.0));
        addSample(new MeterReading(3, "C101", "2024-03", LocalDate.of(2024, 3, 31), 1505.0, 1725.0, 220.0));
        addSample(new MeterReading(4, "C101", "2024-04", LocalDate.of(2024, 4, 30), 1725.0, 1995.0, 270.0));

        // C103 Domestic High
        addSample(new MeterReading(9, "C103", "2024-01", LocalDate.of(2024, 1, 31), 2100.0, 2380.0, 280.0));
        addSample(new MeterReading(10, "C103", "2024-02", LocalDate.of(2024, 2, 29), 2380.0, 2720.0, 340.0));
        addSample(new MeterReading(11, "C103", "2024-03", LocalDate.of(2024, 3, 31), 2720.0, 3260.0, 540.0));
        addSample(new MeterReading(12, "C103", "2024-04", LocalDate.of(2024, 4, 30), 3260.0, 3910.0, 650.0));

        // C201 Commercial IT Hub
        addSample(new MeterReading(21, "C201", "2024-01", LocalDate.of(2024, 1, 31), 12500.0, 13350.0, 850.0));
        addSample(new MeterReading(22, "C201", "2024-02", LocalDate.of(2024, 2, 29), 13350.0, 14280.0, 930.0));
        addSample(new MeterReading(23, "C201", "2024-03", LocalDate.of(2024, 3, 31), 14280.0, 15380.0, 1100.0));
        addSample(new MeterReading(24, "C201", "2024-04", LocalDate.of(2024, 4, 30), 15380.0, 16640.0, 1260.0));

        // C301 Industrial Heavy Steels
        addSample(new MeterReading(33, "C301", "2024-01", LocalDate.of(2024, 1, 31), 45000.0, 49200.0, 4200.0));
        addSample(new MeterReading(34, "C301", "2024-02", LocalDate.of(2024, 2, 29), 49200.0, 53800.0, 4600.0));
        addSample(new MeterReading(35, "C301", "2024-03", LocalDate.of(2024, 3, 31), 53800.0, 59100.0, 5300.0));
        addSample(new MeterReading(36, "C301", "2024-04", LocalDate.of(2024, 4, 30), 59100.0, 64950.0, 5850.0));

        // C401 Agricultural
        addSample(new MeterReading(41, "C401", "2024-01", LocalDate.of(2024, 1, 31), 6100.0, 6450.0, 350.0));
        addSample(new MeterReading(42, "C401", "2024-02", LocalDate.of(2024, 2, 29), 6450.0, 6880.0, 430.0));
        addSample(new MeterReading(43, "C401", "2024-03", LocalDate.of(2024, 3, 31), 6880.0, 7420.0, 540.0));
        addSample(new MeterReading(44, "C401", "2024-04", LocalDate.of(2024, 4, 30), 7420.0, 8080.0, 660.0));
    }

    private static void addSample(MeterReading r) {
        readingStore.add(r);
        if (r.getReadingId() >= nextReadingId) {
            nextReadingId = r.getReadingId() + 1;
        }
    }

    public synchronized boolean recordReading(MeterReading reading) {
        if (reading.getReadingId() <= 0) {
            reading.setReadingId(nextReadingId++);
        }
        // Requirement 3: Consumption = current - previous
        reading.setUnitsConsumed(reading.calculateUnitsConsumed());
        readingStore.add(reading);

        if (DBConnection.isDriverAvailable()) {
            String sql = "INSERT INTO meter_readings (reading_id, consumer_id, billing_month, reading_date, previous_reading, current_reading, units_consumed) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, reading.getReadingId());
                stmt.setString(2, reading.getConsumerId());
                stmt.setString(3, reading.getBillingMonth());
                stmt.setDate(4, java.sql.Date.valueOf(reading.getReadingDate()));
                stmt.setDouble(5, reading.getPreviousReading());
                stmt.setDouble(6, reading.getCurrentReading());
                stmt.setDouble(7, reading.getUnitsConsumed());
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.err.println("[MeterReadingDAO] DB Insert Error: " + e.getMessage());
            }
        }
        return true;
    }

    public List<MeterReading> getAllReadings() {
        if (DBConnection.isDriverAvailable()) {
            List<MeterReading> list = new ArrayList<>();
            String sql = "SELECT * FROM meter_readings ORDER BY billing_month DESC, consumer_id";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.err.println("[MeterReadingDAO] DB Query Error: " + e.getMessage());
            }
        }
        return new ArrayList<>(readingStore);
    }

    public List<MeterReading> getReadingsByConsumer(String consumerId) {
        List<MeterReading> results = new ArrayList<>();
        for (MeterReading r : getAllReadings()) {
            if (r.getConsumerId().equalsIgnoreCase(consumerId)) {
                results.add(r);
            }
        }
        return results;
    }

    public List<MeterReading> getReadingsByMonth(String month) {
        List<MeterReading> results = new ArrayList<>();
        for (MeterReading r : getAllReadings()) {
            if (r.getBillingMonth().equalsIgnoreCase(month)) {
                results.add(r);
            }
        }
        return results;
    }

    public MeterReading getLatestReading(String consumerId) {
        List<MeterReading> list = getReadingsByConsumer(consumerId);
        if (list.isEmpty()) return null;
        list.sort((a, b) -> b.getBillingMonth().compareTo(a.getBillingMonth()));
        return list.get(0);
    }

    private MeterReading mapRow(ResultSet rs) throws SQLException {
        MeterReading r = new MeterReading();
        r.setReadingId(rs.getInt("reading_id"));
        r.setConsumerId(rs.getString("consumer_id"));
        r.setBillingMonth(rs.getString("billing_month"));
        Date dt = rs.getDate("reading_date");
        r.setReadingDate(dt != null ? dt.toLocalDate() : LocalDate.now());
        r.setPreviousReading(rs.getDouble("previous_reading"));
        r.setCurrentReading(rs.getDouble("current_reading"));
        r.setUnitsConsumed(rs.getDouble("units_consumed"));
        return r;
    }
}
