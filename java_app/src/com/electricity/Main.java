package com.electricity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * ===============================================================================
 * Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
 * Student Register Number: 732925ITR104
 * 
 * Self-Contained Enterprise Application:
 * - Full OOP Domain Models (Consumer, MeterReading, TariffSlab, Bill)
 * - Complete DAO Persistence Layer with JDBC (SQLite/MySQL) & Built-in Store
 * - Service Layer for Tiered Slab Billing Math & Comprehensive Audits
 * - Interactive Management Console Menu (Options 1 to 10)
 * ===============================================================================
 */

// ===============================================================================
// 1. DOMAIN MODELS
// ===============================================================================

/**
 * Model class representing a Consumer.
 * Functional Requirement 1: Maintain consumer details (ID, name, address, connection type).
 */
class Consumer {
    private String consumerId;
    private String name;
    private String address;
    private String phone;
    private String connectionType; // Domestic, Commercial, Industrial, Agricultural
    private String meterNumber;
    private LocalDate connectionDate;
    private String status;         // Active, Suspended, Disconnected

    public Consumer() {
        this.status = "Active";
        this.connectionDate = LocalDate.now();
    }

    public Consumer(String consumerId, String name, String address, String phone, 
                    String connectionType, String meterNumber, LocalDate connectionDate, String status) {
        this.consumerId = consumerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.connectionType = connectionType;
        this.meterNumber = meterNumber;
        this.connectionDate = connectionDate;
        this.status = status;
    }

    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public String getMeterNumber() { return meterNumber; }
    public void setMeterNumber(String meterNumber) { this.meterNumber = meterNumber; }

    public LocalDate getConnectionDate() { return connectionDate; }
    public void setConnectionDate(LocalDate connectionDate) { this.connectionDate = connectionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Type: %-12s | Meter: %-12s | Phone: %s | Status: %s",
                consumerId, name, connectionType, meterNumber, phone, status);
    }
}

/**
 * Model class representing a Monthly Meter Reading.
 * Functional Requirement 2: Record previous and current meter readings for each billing period.
 * Functional Requirement 3: Calculate electricity consumption (current - previous).
 */
class MeterReading {
    private int readingId;
    private String consumerId;
    private String billingMonth;
    private LocalDate readingDate;
    private double previousReading;
    private double currentReading;
    private double unitsConsumed;

    public MeterReading() {}

    public MeterReading(int readingId, String consumerId, String billingMonth, LocalDate readingDate,
                        double previousReading, double currentReading) {
        this.readingId = readingId;
        this.consumerId = consumerId;
        this.billingMonth = billingMonth;
        this.readingDate = readingDate;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.unitsConsumed = calculateUnitsConsumed();
    }

    public MeterReading(int readingId, String consumerId, String billingMonth, LocalDate readingDate,
                        double previousReading, double currentReading, double unitsConsumed) {
        this.readingId = readingId;
        this.consumerId = consumerId;
        this.billingMonth = billingMonth;
        this.readingDate = readingDate;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.unitsConsumed = unitsConsumed;
    }

    /**
     * Requirement 3: Units Consumed = Current Reading - Previous Reading
     */
    public double calculateUnitsConsumed() {
        if (currentReading < previousReading) {
            throw new IllegalArgumentException("Current reading cannot be less than previous reading.");
        }
        return Math.round((currentReading - previousReading) * 100.0) / 100.0;
    }

    public int getReadingId() { return readingId; }
    public void setReadingId(int readingId) { this.readingId = readingId; }

    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public String getBillingMonth() { return billingMonth; }
    public void setBillingMonth(String billingMonth) { this.billingMonth = billingMonth; }

    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public double getPreviousReading() { return previousReading; }
    public void setPreviousReading(double previousReading) { 
        this.previousReading = previousReading;
        this.unitsConsumed = calculateUnitsConsumed();
    }

    public double getCurrentReading() { return currentReading; }
    public void setCurrentReading(double currentReading) { 
        this.currentReading = currentReading;
        this.unitsConsumed = calculateUnitsConsumed();
    }

    public double getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(double unitsConsumed) { this.unitsConsumed = unitsConsumed; }

    @Override
    public String toString() {
        return String.format("Reading #%d | Consumer: %-6s | Month: %s | Prev: %8.2f | Curr: %8.2f | Units: %8.2f",
                readingId, consumerId, billingMonth, previousReading, currentReading, unitsConsumed);
    }
}

/**
 * Model class representing a Tariff Slab.
 */
class TariffSlab {
    private int slabId;
    private String connectionType;
    private int minUnits;
    private int maxUnits;
    private double ratePerUnit;
    private double fixedCharge;
    private double taxPercent;

    public TariffSlab() {}

    public TariffSlab(int slabId, String connectionType, int minUnits, int maxUnits, 
                      double ratePerUnit, double fixedCharge, double taxPercent) {
        this.slabId = slabId;
        this.connectionType = connectionType;
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
        this.ratePerUnit = ratePerUnit;
        this.fixedCharge = fixedCharge;
        this.taxPercent = taxPercent;
    }

    public int getSlabId() { return slabId; }
    public void setSlabId(int slabId) { this.slabId = slabId; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public int getMinUnits() { return minUnits; }
    public void setMinUnits(int minUnits) { this.minUnits = minUnits; }

    public int getMaxUnits() { return maxUnits; }
    public void setMaxUnits(int maxUnits) { this.maxUnits = maxUnits; }

    public double getRatePerUnit() { return ratePerUnit; }
    public void setRatePerUnit(double ratePerUnit) { this.ratePerUnit = ratePerUnit; }

    public double getFixedCharge() { return fixedCharge; }
    public void setFixedCharge(double fixedCharge) { this.fixedCharge = fixedCharge; }

    public double getTaxPercent() { return taxPercent; }
    public void setTaxPercent(double taxPercent) { this.taxPercent = taxPercent; }

    @Override
    public String toString() {
        return String.format("[%s] Units: %4d - %-6s | Rate: Rs. %5.2f/unit | Fixed: Rs. %6.2f | Tax: %.1f%%",
                connectionType, minUnits, (maxUnits >= 999999 ? "Above" : String.valueOf(maxUnits)), 
                ratePerUnit, fixedCharge, taxPercent);
    }
}

/**
 * Model class representing a generated Electricity Bill.
 * Functional Requirement 4: Calculate electricity bills using predefined tariff rates & charges.
 */
class Bill {
    private String billId;
    private int readingId;
    private String consumerId;
    private String billingMonth;
    private double unitsConsumed;
    private double energyCharges;
    private double fixedCharges;
    private double taxAmount;
    private double totalAmount;
    private LocalDate billDate;
    private LocalDate dueDate;
    private String paymentStatus;

    public Bill() {
        this.billDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(15);
        this.paymentStatus = "Unpaid";
    }

    public Bill(String billId, int readingId, String consumerId, String billingMonth,
                double unitsConsumed, double energyCharges, double fixedCharges,
                double taxAmount, double totalAmount, LocalDate billDate, LocalDate dueDate, String paymentStatus) {
        this.billId = billId;
        this.readingId = readingId;
        this.consumerId = consumerId;
        this.billingMonth = billingMonth;
        this.unitsConsumed = unitsConsumed;
        this.energyCharges = energyCharges;
        this.fixedCharges = fixedCharges;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.paymentStatus = paymentStatus;
    }

    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public int getReadingId() { return readingId; }
    public void setReadingId(int readingId) { this.readingId = readingId; }

    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public String getBillingMonth() { return billingMonth; }
    public void setBillingMonth(String billingMonth) { this.billingMonth = billingMonth; }

    public double getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(double unitsConsumed) { this.unitsConsumed = unitsConsumed; }

    public double getEnergyCharges() { return energyCharges; }
    public void setEnergyCharges(double energyCharges) { this.energyCharges = energyCharges; }

    public double getFixedCharges() { return fixedCharges; }
    public void setFixedCharges(double fixedCharges) { this.fixedCharges = fixedCharges; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    @Override
    public String toString() {
        return String.format("Bill: %-16s | Consumer: %-6s | Month: %s | Units: %7.2f | Total: Rs. %9.2f | Status: %s",
                billId, consumerId, billingMonth, unitsConsumed, totalAmount, paymentStatus);
    }
}

// ===============================================================================
// 2. DATA ACCESS LAYER (DAO)
// ===============================================================================

class DBConnection {
    private static final String SQLITE_RELATIVE_PATH = "../database/electricity.db";
    private static final String SQLITE_URL_PREFIX = "jdbc:sqlite:";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/electricity_db?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASS = "password";

    private static boolean driverAvailable = false;
    private static boolean checkedDriver = false;

    static {
        checkDrivers();
    }

    private static void checkDrivers() {
        if (checkedDriver) return;
        try {
            Class.forName("org.sqlite.JDBC");
            driverAvailable = true;
        } catch (ClassNotFoundException e1) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverAvailable = true;
            } catch (ClassNotFoundException e2) {
                driverAvailable = false;
            }
        }
        checkedDriver = true;
    }

    public static boolean isDriverAvailable() { return driverAvailable; }

    public static Connection getConnection() throws SQLException {
        if (!driverAvailable) return null;
        try {
            File dbFile = new File(SQLITE_RELATIVE_PATH);
            String dbPath = dbFile.exists() ? dbFile.getAbsolutePath() : "electricity.db";
            return DriverManager.getConnection(SQLITE_URL_PREFIX + dbPath);
        } catch (SQLException e) {
            return DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASS);
        }
    }
}

class ConsumerDAO {
    private static final Map<String, Consumer> consumerStore = new LinkedHashMap<>();

    static {
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
                stmt.setDate(7, Date.valueOf(consumer.getConnectionDate()));
                stmt.setString(8, consumer.getStatus());
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                // Handled gracefully
            }
        }
        return true;
    }

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
                // Handled gracefully
            }
        }
        return true;
    }

    public Consumer getConsumerById(String consumerId) {
        if (consumerId == null) return null;
        if (DBConnection.isDriverAvailable()) {
            String sql = "SELECT * FROM consumers WHERE UPPER(consumer_id) = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, consumerId.toUpperCase());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) return mapRow(rs);
            } catch (SQLException e) {
                // Handled gracefully
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
                while (rs.next()) list.add(mapRow(rs));
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                // Handled gracefully
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
        Date dt = rs.getDate("connection_date");
        c.setConnectionDate(dt != null ? dt.toLocalDate() : LocalDate.now());
        c.setStatus(rs.getString("status"));
        return c;
    }
}

class MeterReadingDAO {
    private static final List<MeterReading> readingStore = new ArrayList<>();
    private static int nextReadingId = 100;

    static {
        initDefaultReadings();
    }

    private static void initDefaultReadings() {
        addSample(new MeterReading(1, "C101", "2024-01", LocalDate.of(2024, 1, 31), 1200.0, 1340.0, 140.0));
        addSample(new MeterReading(2, "C101", "2024-02", LocalDate.of(2024, 2, 29), 1340.0, 1505.0, 165.0));
        addSample(new MeterReading(3, "C101", "2024-03", LocalDate.of(2024, 3, 31), 1505.0, 1725.0, 220.0));
        addSample(new MeterReading(4, "C101", "2024-04", LocalDate.of(2024, 4, 30), 1725.0, 1995.0, 270.0));

        addSample(new MeterReading(9, "C103", "2024-01", LocalDate.of(2024, 1, 31), 2100.0, 2380.0, 280.0));
        addSample(new MeterReading(10, "C103", "2024-02", LocalDate.of(2024, 2, 29), 2380.0, 2720.0, 340.0));
        addSample(new MeterReading(11, "C103", "2024-03", LocalDate.of(2024, 3, 31), 2720.0, 3260.0, 540.0));
        addSample(new MeterReading(12, "C103", "2024-04", LocalDate.of(2024, 4, 30), 3260.0, 3910.0, 650.0));

        addSample(new MeterReading(21, "C201", "2024-01", LocalDate.of(2024, 1, 31), 12500.0, 13350.0, 850.0));
        addSample(new MeterReading(22, "C201", "2024-02", LocalDate.of(2024, 2, 29), 13350.0, 14280.0, 930.0));
        addSample(new MeterReading(23, "C201", "2024-03", LocalDate.of(2024, 3, 31), 14280.0, 15380.0, 1100.0));
        addSample(new MeterReading(24, "C201", "2024-04", LocalDate.of(2024, 4, 30), 15380.0, 16640.0, 1260.0));

        addSample(new MeterReading(33, "C301", "2024-01", LocalDate.of(2024, 1, 31), 45000.0, 49200.0, 4200.0));
        addSample(new MeterReading(34, "C301", "2024-02", LocalDate.of(2024, 2, 29), 49200.0, 53800.0, 4600.0));
        addSample(new MeterReading(35, "C301", "2024-03", LocalDate.of(2024, 3, 31), 53800.0, 59100.0, 5300.0));
        addSample(new MeterReading(36, "C301", "2024-04", LocalDate.of(2024, 4, 30), 59100.0, 64950.0, 5850.0));

        addSample(new MeterReading(41, "C401", "2024-01", LocalDate.of(2024, 1, 31), 6100.0, 6450.0, 350.0));
        addSample(new MeterReading(42, "C401", "2024-02", LocalDate.of(2024, 2, 29), 6450.0, 6880.0, 430.0));
        addSample(new MeterReading(43, "C401", "2024-03", LocalDate.of(2024, 3, 31), 6880.0, 7420.0, 540.0));
        addSample(new MeterReading(44, "C401", "2024-04", LocalDate.of(2024, 4, 30), 7420.0, 8080.0, 660.0));
    }

    private static void addSample(MeterReading r) {
        readingStore.add(r);
        if (r.getReadingId() >= nextReadingId) nextReadingId = r.getReadingId() + 1;
    }

    public synchronized boolean recordReading(MeterReading reading) {
        if (reading.getReadingId() <= 0) reading.setReadingId(nextReadingId++);
        reading.setUnitsConsumed(reading.calculateUnitsConsumed());
        readingStore.add(reading);

        if (DBConnection.isDriverAvailable()) {
            String sql = "INSERT INTO meter_readings (reading_id, consumer_id, billing_month, reading_date, previous_reading, current_reading, units_consumed) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, reading.getReadingId());
                stmt.setString(2, reading.getConsumerId());
                stmt.setString(3, reading.getBillingMonth());
                stmt.setDate(4, Date.valueOf(reading.getReadingDate()));
                stmt.setDouble(5, reading.getPreviousReading());
                stmt.setDouble(6, reading.getCurrentReading());
                stmt.setDouble(7, reading.getUnitsConsumed());
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                // Handled gracefully
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
                while (rs.next()) list.add(mapRow(rs));
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                // Handled gracefully
            }
        }
        return new ArrayList<>(readingStore);
    }

    public List<MeterReading> getReadingsByConsumer(String consumerId) {
        List<MeterReading> results = new ArrayList<>();
        for (MeterReading r : getAllReadings()) {
            if (r.getConsumerId().equalsIgnoreCase(consumerId)) results.add(r);
        }
        return results;
    }

    public List<MeterReading> getReadingsByMonth(String month) {
        List<MeterReading> results = new ArrayList<>();
        for (MeterReading r : getAllReadings()) {
            if (r.getBillingMonth().equalsIgnoreCase(month)) results.add(r);
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

class BillDAO {
    private static final Map<String, Bill> billStore = new LinkedHashMap<>();

    static {
        initDefaultBills();
    }

    private static void initDefaultBills() {
        addSample(new Bill("BILL-202401-C101", 1, "C101", "2024-01", 140.0, 558.00, 75.00, 31.65, 664.65, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202402-C101", 2, "C101", "2024-02", 165.0, 688.00, 75.00, 38.15, 801.15, LocalDate.of(2024, 3, 5), LocalDate.of(2024, 3, 20), "Paid"));
        addSample(new Bill("BILL-202403-C101", 3, "C101", "2024-03", 220.0, 974.00, 75.00, 52.45, 1101.45, LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 20), "Paid"));
        addSample(new Bill("BILL-202404-C101", 4, "C101", "2024-04", 270.0, 1234.00, 75.00, 65.45, 1374.45, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));

        addSample(new Bill("BILL-202401-C103", 9, "C103", "2024-01", 280.0, 1286.00, 75.00, 68.05, 1429.05, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202404-C103", 12, "C103", "2024-04", 650.0, 4360.00, 150.00, 225.50, 4735.50, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));

        addSample(new Bill("BILL-202401-C201", 21, "C201", "2024-01", 850.0, 9100.00, 500.00, 864.00, 10464.00, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202404-C201", 24, "C201", "2024-04", 1260.0, 14635.00, 500.00, 1362.15, 16497.15, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));

        addSample(new Bill("BILL-202401-C301", 33, "C301", "2024-01", 4200.0, 49000.00, 1500.00, 6060.00, 56560.00, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202404-C301", 36, "C301", "2024-04", 5850.0, 69625.00, 1500.00, 8535.00, 79660.00, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));

        addSample(new Bill("BILL-202401-C401", 41, "C401", "2024-01", 350.0, 525.00, 30.00, 11.10, 566.10, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202404-C401", 44, "C401", "2024-04", 660.0, 1198.00, 50.00, 24.96, 1272.96, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));
    }

    private static void addSample(Bill b) {
        billStore.put(b.getBillId(), b);
    }

    public boolean saveBill(Bill bill) {
        billStore.put(bill.getBillId(), bill);
        if (DBConnection.isDriverAvailable()) {
            String sql = "INSERT INTO bills (bill_id, reading_id, consumer_id, billing_month, units_consumed, energy_charges, fixed_charges, tax_amount, total_amount, bill_date, due_date, payment_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bill.getBillId());
                stmt.setInt(2, bill.getReadingId());
                stmt.setString(3, bill.getConsumerId());
                stmt.setString(4, bill.getBillingMonth());
                stmt.setDouble(5, bill.getUnitsConsumed());
                stmt.setDouble(6, bill.getEnergyCharges());
                stmt.setDouble(7, bill.getFixedCharges());
                stmt.setDouble(8, bill.getTaxAmount());
                stmt.setDouble(9, bill.getTotalAmount());
                stmt.setDate(10, Date.valueOf(bill.getBillDate()));
                stmt.setDate(11, Date.valueOf(bill.getDueDate()));
                stmt.setString(12, bill.getPaymentStatus());
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                // Handled gracefully
            }
        }
        return true;
    }

    public List<Bill> getAllBills() {
        if (DBConnection.isDriverAvailable()) {
            List<Bill> list = new ArrayList<>();
            String sql = "SELECT * FROM bills ORDER BY billing_month DESC, consumer_id";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) list.add(mapRow(rs));
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                // Handled gracefully
            }
        }
        return new ArrayList<>(billStore.values());
    }

    public List<Bill> getBillsByConsumer(String consumerId) {
        List<Bill> list = new ArrayList<>();
        for (Bill b : getAllBills()) {
            if (b.getConsumerId().equalsIgnoreCase(consumerId)) list.add(b);
        }
        return list;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getString("bill_id"));
        b.setReadingId(rs.getInt("reading_id"));
        b.setConsumerId(rs.getString("consumer_id"));
        b.setBillingMonth(rs.getString("billing_month"));
        b.setUnitsConsumed(rs.getDouble("units_consumed"));
        b.setEnergyCharges(rs.getDouble("energy_charges"));
        b.setFixedCharges(rs.getDouble("fixed_charges"));
        b.setTaxAmount(rs.getDouble("tax_amount"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        Date bDate = rs.getDate("bill_date");
        b.setBillDate(bDate != null ? bDate.toLocalDate() : LocalDate.now());
        Date dDate = rs.getDate("due_date");
        b.setDueDate(dDate != null ? dDate.toLocalDate() : LocalDate.now().plusDays(15));
        b.setPaymentStatus(rs.getString("payment_status"));
        return b;
    }
}

// ===============================================================================
// 3. SERVICE LAYER (BUSINESS LOGIC)
// ===============================================================================

class BillingService {
    private final ConsumerDAO consumerDAO;
    private final MeterReadingDAO meterReadingDAO;
    private final BillDAO billDAO;

    public BillingService(ConsumerDAO cDAO, MeterReadingDAO mrDAO, BillDAO bDAO) {
        this.consumerDAO = cDAO;
        this.meterReadingDAO = mrDAO;
        this.billDAO = bDAO;
    }

    public Bill generateBillForReading(int readingId) {
        MeterReading reading = null;
        for (MeterReading r : meterReadingDAO.getAllReadings()) {
            if (r.getReadingId() == readingId) {
                reading = r;
                break;
            }
        }
        if (reading == null) throw new IllegalArgumentException("Meter Reading ID " + readingId + " not found.");

        Consumer consumer = consumerDAO.getConsumerById(reading.getConsumerId());
        if (consumer == null) throw new IllegalArgumentException("Consumer ID " + reading.getConsumerId() + " not found.");

        double units = reading.getUnitsConsumed();
        String connType = consumer.getConnectionType();
        double energyCharges = 0.0;
        double fixedCharges = 0.0;
        double taxRate = 0.05;

        switch (connType.trim()) {
            case "Domestic":
                taxRate = 0.05;
                if (units <= 100) {
                    energyCharges = units * 3.50;
                    fixedCharges = 50.00;
                } else if (units <= 300) {
                    energyCharges = (100 * 3.50) + ((units - 100) * 5.20);
                    fixedCharges = 75.00;
                } else if (units <= 500) {
                    energyCharges = (100 * 3.50) + (200 * 5.20) + ((units - 300) * 7.50);
                    fixedCharges = 100.00;
                } else {
                    energyCharges = (100 * 3.50) + (200 * 5.20) + (200 * 7.50) + ((units - 500) * 9.80);
                    fixedCharges = 150.00;
                }
                break;

            case "Commercial":
                taxRate = 0.09;
                if (units <= 200) {
                    energyCharges = units * 8.50;
                    fixedCharges = 200.00;
                } else if (units <= 750) {
                    energyCharges = (200 * 8.50) + ((units - 200) * 11.00);
                    fixedCharges = 350.00;
                } else {
                    energyCharges = (200 * 8.50) + (550 * 11.00) + ((units - 750) * 13.50);
                    fixedCharges = 500.00;
                }
                break;

            case "Industrial":
                taxRate = 0.12;
                if (units <= 1000) {
                    energyCharges = units * 9.00;
                    fixedCharges = 800.00;
                } else {
                    energyCharges = (1000 * 9.00) + ((units - 1000) * 12.50);
                    fixedCharges = 1500.00;
                }
                break;

            case "Agricultural":
                taxRate = 0.02;
                if (units <= 500) {
                    energyCharges = units * 1.50;
                    fixedCharges = 30.00;
                } else {
                    energyCharges = (500 * 1.50) + ((units - 500) * 2.80);
                    fixedCharges = 50.00;
                }
                break;

            default:
                energyCharges = units * 6.00;
                fixedCharges = 100.00;
                taxRate = 0.05;
        }

        energyCharges = Math.round(energyCharges * 100.0) / 100.0;
        double taxAmount = Math.round((energyCharges + fixedCharges) * taxRate * 100.0) / 100.0;
        double totalAmount = Math.round((energyCharges + fixedCharges + taxAmount) * 100.0) / 100.0;

        String billId = String.format("BILL-%s-%s", 
                reading.getBillingMonth().replace("-", ""), consumer.getConsumerId());

        LocalDate billDate = LocalDate.now();
        LocalDate dueDate = billDate.plusDays(15);

        Bill bill = new Bill(billId, reading.getReadingId(), consumer.getConsumerId(),
                reading.getBillingMonth(), units, energyCharges, fixedCharges, taxAmount, totalAmount,
                billDate, dueDate, "Unpaid");

        billDAO.saveBill(bill);
        return bill;
    }
}

class ReportService {
    private final ConsumerDAO consumerDAO;
    private final MeterReadingDAO meterReadingDAO;
    private final BillDAO billDAO;

    public ReportService(ConsumerDAO cDAO, MeterReadingDAO mrDAO, BillDAO bDAO) {
        this.consumerDAO = cDAO;
        this.meterReadingDAO = mrDAO;
        this.billDAO = bDAO;
    }

    public void printConsumerBill(String consumerId, String month) {
        Consumer consumer = consumerDAO.getConsumerById(consumerId);
        if (consumer == null) {
            System.out.println("[Error] Consumer not found with ID: " + consumerId);
            return;
        }

        List<Bill> bills = billDAO.getBillsByConsumer(consumerId);
        Bill selectedBill = null;
        for (Bill b : bills) {
            if (month == null || b.getBillingMonth().equalsIgnoreCase(month)) {
                selectedBill = b;
                break;
            }
        }

        if (selectedBill == null) {
            System.out.println("[Notice] No bill record found for Consumer " + consumerId + (month != null ? " in " + month : ""));
            return;
        }

        System.out.println("\n==========================================================================");
        System.out.println("                   ⚡ ELECTRICITY DISTRIBUTION BOARD ⚡                   ");
        System.out.println("                         OFFICIAL CONSUMER INVOICE                         ");
        System.out.println("==========================================================================");
        System.out.printf(" Bill Number     : %-25s Bill Date  : %s%n", selectedBill.getBillId(), selectedBill.getBillDate());
        System.out.printf(" Billing Month   : %-25s Due Date   : %s%n", selectedBill.getBillingMonth(), selectedBill.getDueDate());
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(" Consumer ID     : %-25s Category   : %s%n", consumer.getConsumerId(), consumer.getConnectionType());
        System.out.printf(" Consumer Name   : %-25s Meter No   : %s%n", consumer.getName(), consumer.getMeterNumber());
        System.out.printf(" Address         : %-45s%n", consumer.getAddress());
        System.out.printf(" Contact Phone   : %-45s%n", consumer.getPhone());
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(" PARTICULARS & CHARGES BREAKDOWN                                AMOUNT (Rs)");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(" Units Consumed (kWh)            : %12.2f units%n", selectedBill.getUnitsConsumed());
        System.out.printf(" Energy Charges (Tariff Slabs)   :                     %12.2f%n", selectedBill.getEnergyCharges());
        System.out.printf(" Fixed Demand Charges            :                     %12.2f%n", selectedBill.getFixedCharges());
        System.out.printf(" Electricity Duty / Tax / Cess   :                     %12.2f%n", selectedBill.getTaxAmount());
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(" TOTAL AMOUNT PAYABLE            :                 Rs. %12.2f%n", selectedBill.getTotalAmount());
        System.out.printf(" Payment Status                  :                     %12s%n", selectedBill.getPaymentStatus().toUpperCase());
        System.out.println("==========================================================================\n");
    }

    public void printMonthlyConsumptionReport(String month) {
        List<MeterReading> readings = (month != null && !month.trim().isEmpty()) 
                ? meterReadingDAO.getReadingsByMonth(month.trim()) 
                : meterReadingDAO.getAllReadings();

        if (readings.isEmpty()) {
            System.out.println("[Notice] No consumption records found for period: " + (month != null ? month : "ALL"));
            return;
        }

        double totalUnits = 0;
        double minUnits = Double.MAX_VALUE;
        double maxUnits = Double.MIN_VALUE;

        Map<String, Double> categoryUnits = new HashMap<>();
        Map<String, Integer> categoryCounts = new HashMap<>();

        for (MeterReading r : readings) {
            double u = r.getUnitsConsumed();
            totalUnits += u;
            if (u < minUnits) minUnits = u;
            if (u > maxUnits) maxUnits = u;

            Consumer c = consumerDAO.getConsumerById(r.getConsumerId());
            String cat = (c != null) ? c.getConnectionType() : "Unknown";
            categoryUnits.put(cat, categoryUnits.getOrDefault(cat, 0.0) + u);
            categoryCounts.put(cat, categoryCounts.getOrDefault(cat, 0) + 1);
        }

        double avgUnits = totalUnits / readings.size();

        System.out.println("\n==========================================================================");
        System.out.println("                     MONTHLY CONSUMPTION AUDIT REPORT                     ");
        System.out.println("                          Period: " + (month != null ? month : "All Recorded Periods"));
        System.out.println("==========================================================================");
        System.out.printf(" Total Meter Readings Recorded   : %d%n", readings.size());
        System.out.printf(" Total Electricity Consumed      : %.2f kWh (Units)%n", totalUnits);
        System.out.printf(" Average Consumption per Meter   : %.2f kWh (Units)%n", avgUnits);
        System.out.printf(" Minimum Consumption Recorded    : %.2f kWh (Units)%n", minUnits);
        System.out.printf(" Peak (Maximum) Meter Reading    : %.2f kWh (Units)%n", maxUnits);
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(" CATEGORY-WISE BREAKDOWN:");
        System.out.printf(" %-16s | %-12s | %-16s | %-14s%n", "Category", "Consumer Ct", "Total Units (kWh)", "Share of Total");
        System.out.println(" -------------------------------------------------------------------------");
        for (Map.Entry<String, Double> entry : categoryUnits.entrySet()) {
            String cat = entry.getKey();
            double units = entry.getValue();
            int count = categoryCounts.get(cat);
            double pct = (units / totalUnits) * 100.0;
            System.out.printf(" %-16s | %-12d | %16.2f | %13.2f%%%n", cat, count, units, pct);
        }
        System.out.println("==========================================================================\n");
    }

    public void printHighConsumptionReport(double thresholdUnits) {
        List<MeterReading> readings = meterReadingDAO.getAllReadings();
        List<MeterReading> highConsumers = new ArrayList<>();

        for (MeterReading r : readings) {
            if (r.getUnitsConsumed() >= thresholdUnits) highConsumers.add(r);
        }
        highConsumers.sort((a, b) -> Double.compare(b.getUnitsConsumed(), a.getUnitsConsumed()));

        System.out.println("\n==========================================================================");
        System.out.println("                     HIGH-CONSUMPTION CONSUMERS ALERT                     ");
        System.out.println("                 Threshold: >= " + thresholdUnits + " kWh Units");
        System.out.println("==========================================================================");
        System.out.printf(" %-8s | %-18s | %-12s | %-7s | %-10s | %-10s%n", 
                "ID", "Name", "Category", "Month", "Units (kWh)", "Alert Level");
        System.out.println("--------------------------------------------------------------------------");

        if (highConsumers.isEmpty()) {
            System.out.println(" No consumers exceeded the threshold of " + thresholdUnits + " units.");
        } else {
            for (MeterReading r : highConsumers) {
                Consumer c = consumerDAO.getConsumerById(r.getConsumerId());
                String name = (c != null) ? c.getName() : "Unknown";
                String cat = (c != null) ? c.getConnectionType() : "Unknown";
                String alertLevel = (r.getUnitsConsumed() >= 3000) ? "[CRITICAL]" : ((r.getUnitsConsumed() >= 1000) ? "[SEVERE]" : "[HIGH]");

                System.out.printf(" %-8s | %-18s | %-12s | %-7s | %10.2f | %-10s%n",
                        r.getConsumerId(),
                        (name.length() > 18 ? name.substring(0, 15) + "..." : name),
                        cat, r.getBillingMonth(), r.getUnitsConsumed(), alertLevel);
            }
        }
        System.out.println("==========================================================================\n");
    }

    public void exportDataToCSV(String filePath) {
        List<MeterReading> readings = meterReadingDAO.getAllReadings();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("consumer_id,name,connection_type,address,meter_number,reading_id,billing_month,reading_date,previous_reading,current_reading,units_consumed,energy_charges,fixed_charges,tax_amount,bill_total,payment_status");
            for (MeterReading mr : readings) {
                Consumer c = consumerDAO.getConsumerById(mr.getConsumerId());
                Bill b = null;
                for (Bill bill : billDAO.getAllBills()) {
                    if (bill.getReadingId() == mr.getReadingId()) { b = bill; break; }
                }
                String name = (c != null) ? c.getName() : "Unknown";
                String cat = (c != null) ? c.getConnectionType() : "Domestic";
                String addr = (c != null) ? c.getAddress().replace(",", ";") : "";
                String meter = (c != null) ? c.getMeterNumber() : "";
                double ec = (b != null) ? b.getEnergyCharges() : 0.0;
                double fc = (b != null) ? b.getFixedCharges() : 0.0;
                double tax = (b != null) ? b.getTaxAmount() : 0.0;
                double tot = (b != null) ? b.getTotalAmount() : 0.0;
                String status = (b != null) ? b.getPaymentStatus() : "Unbilled";

                writer.printf("%s,\"%s\",%s,\"%s\",%s,%d,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s%n",
                        mr.getConsumerId(), name, cat, addr, meter, mr.getReadingId(),
                        mr.getBillingMonth(), mr.getReadingDate(), mr.getPreviousReading(),
                        mr.getCurrentReading(), mr.getUnitsConsumed(), ec, fc, tax, tot, status);
            }
            System.out.println("[Success] Analytical dataset successfully exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("[Error] Failed to export CSV: " + e.getMessage());
        }
    }
}

// ===============================================================================
// 4. MAIN INTERACTIVE APPLICATION
// ===============================================================================

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ConsumerDAO consumerDAO = new ConsumerDAO();
    private static final MeterReadingDAO meterReadingDAO = new MeterReadingDAO();
    private static final BillDAO billDAO = new BillDAO();
    private static final BillingService billingService = new BillingService(consumerDAO, meterReadingDAO, billDAO);
    private static final ReportService reportService = new ReportService(consumerDAO, meterReadingDAO, billDAO);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printBanner();
            printMenu();
            System.out.print(" Enter your choice (0-10): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleRegisterConsumer();
                    break;
                case "2":
                    handleViewAllConsumers();
                    break;
                case "3":
                    handleUpdateConsumer();
                    break;
                case "4":
                    handleRecordMeterReading();
                    break;
                case "5":
                    handleGenerateBill();
                    break;
                case "6":
                    handleViewConsumerBill();
                    break;
                case "7":
                    handleMonthlyConsumptionReport();
                    break;
                case "8":
                    handleHighConsumptionConsumers();
                    break;
                case "9":
                    handleViewTariffSlabs();
                    break;
                case "10":
                    handleExportData();
                    break;
                case "0":
                    System.out.println("\n[!] Thank you for using the Electricity Management System. Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("\n[X] Invalid selection. Please choose an option from 0 to 10.");
            }

            if (running) {
                System.out.print("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void printBanner() {
        System.out.println("\n===============================================================================");
        System.out.println("  ⚡ ELECTRICITY CONSUMPTION MANAGEMENT & ENERGY USAGE ANALYSIS SYSTEM ⚡     ");
        System.out.println("                 Scenario 14 | Register No.: 732925ITR104                       ");
        System.out.println("===============================================================================");
    }

    private static void printMenu() {
        System.out.println(" 1. Register New Consumer Detail               (FR1)");
        System.out.println(" 2. View All Consumer Master Records           (FR1)");
        System.out.println(" 3. Update Existing Consumer Details           (FR1)");
        System.out.println(" 4. Record Meter Reading & Compute Consumption (FR2, FR3, BR2, BR6)");
        System.out.println(" 5. Calculate & Generate Electricity Bill      (FR4, FR5, BR4)");
        System.out.println(" 6. View Consumer-Wise Bill / Invoice          (FR5)");
        System.out.println(" 7. Generate Monthly Consumption Audit Report  (FR6)");
        System.out.println(" 8. List High-Consumption Consumers (Threshold)(FR6, BR5)");
        System.out.println(" 9. Display Predefined Tariff Slabs & Rates    (FR4)");
        System.out.println(" 10. Export Analytical Dataset for Python Visuals(FR7)");
        System.out.println(" 0. Exit System");
        System.out.println("-------------------------------------------------------------------------------");
    }

    private static void handleRegisterConsumer() {
        System.out.println("\n--- [Register New Consumer] ---");
        System.out.print("Enter Consumer ID (e.g. C106): ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("[X] Consumer ID cannot be empty.");
            return;
        }
        if (consumerDAO.getConsumerById(id) != null) {
            System.out.println("[X] Consumer with ID " + id + " already exists!");
            return;
        }

        System.out.print("Enter Consumer Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Premise Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Enter Contact Phone: ");
        String phone = scanner.nextLine().trim();

        System.out.println("Select Connection Type:");
        System.out.println(" 1. Domestic\n 2. Commercial\n 3. Industrial\n 4. Agricultural");
        System.out.print("Enter Choice (1-4): ");
        String catChoice = scanner.nextLine().trim();
        String connType = "Domestic";
        switch (catChoice) {
            case "2": connType = "Commercial"; break;
            case "3": connType = "Industrial"; break;
            case "4": connType = "Agricultural"; break;
            default: connType = "Domestic"; break;
        }

        System.out.print("Enter Meter Serial Number: ");
        String meterNo = scanner.nextLine().trim();
        if (meterNo.isEmpty()) meterNo = "MTR-" + connType.substring(0, 3).toUpperCase() + "-" + id;

        Consumer c = new Consumer(id, name, address, phone, connType, meterNo, LocalDate.now(), "Active");
        consumerDAO.addConsumer(c);
        System.out.println("\n[✓] Consumer registered successfully: " + c.getName() + " (" + c.getConnectionType() + ")");
    }

    private static void handleViewAllConsumers() {
        System.out.println("\n--- [All Registered Consumers] ---");
        List<Consumer> consumers = consumerDAO.getAllConsumers();
        System.out.printf(" %-8s | %-22s | %-13s | %-14s | %-12s | %s%n", 
                "ID", "Name", "Category", "Meter No", "Phone", "Status");
        System.out.println(" -----------------------------------------------------------------------------------------");
        for (Consumer c : consumers) {
            System.out.printf(" %-8s | %-22s | %-13s | %-14s | %-12s | %s%n",
                    c.getConsumerId(),
                    (c.getName().length() > 22 ? c.getName().substring(0, 19) + "..." : c.getName()),
                    c.getConnectionType(), c.getMeterNumber(), c.getPhone(), c.getStatus());
        }
        System.out.println(" Total Registered Consumers: " + consumers.size());
    }

    private static void handleUpdateConsumer() {
        System.out.println("\n--- [Update Consumer Details] ---");
        System.out.print("Enter Consumer ID to Update (e.g. C101): ");
        String id = scanner.nextLine().trim();
        Consumer c = consumerDAO.getConsumerById(id);
        if (c == null) {
            System.out.println("[X] Consumer not found with ID: " + id);
            return;
        }

        System.out.println("Current Details: " + c.getName() + " | " + c.getConnectionType() + " | " + c.getAddress() + " | Phone: " + c.getPhone() + " | Status: " + c.getStatus());
        System.out.print("Enter New Name [Press ENTER to keep '" + c.getName() + "']: ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) c.setName(newName);

        System.out.print("Enter New Address [Press ENTER to keep '" + c.getAddress() + "']: ");
        String newAddr = scanner.nextLine().trim();
        if (!newAddr.isEmpty()) c.setAddress(newAddr);

        System.out.print("Enter New Phone [Press ENTER to keep '" + c.getPhone() + "']: ");
        String newPhone = scanner.nextLine().trim();
        if (!newPhone.isEmpty()) c.setPhone(newPhone);

        System.out.println("Change Connection Type? (1: Domestic, 2: Commercial, 3: Industrial, 4: Agricultural, Enter to keep '" + c.getConnectionType() + "'):");
        String catInput = scanner.nextLine().trim();
        if ("1".equals(catInput)) c.setConnectionType("Domestic");
        else if ("2".equals(catInput)) c.setConnectionType("Commercial");
        else if ("3".equals(catInput)) c.setConnectionType("Industrial");
        else if ("4".equals(catInput)) c.setConnectionType("Agricultural");

        System.out.println("Change Status? (1: Active, 2: Suspended, 3: Disconnected, Enter to keep '" + c.getStatus() + "'):");
        String statInput = scanner.nextLine().trim();
        if ("1".equals(statInput)) c.setStatus("Active");
        else if ("2".equals(statInput)) c.setStatus("Suspended");
        else if ("3".equals(statInput)) c.setStatus("Disconnected");

        consumerDAO.updateConsumer(c);
        System.out.println("\n[✓] Consumer record updated successfully for ID: " + c.getConsumerId());
    }

    private static void handleRecordMeterReading() {
        System.out.println("\n--- [Record Meter Reading] ---");
        System.out.print("Enter Consumer ID: ");
        String id = scanner.nextLine().trim();
        Consumer c = consumerDAO.getConsumerById(id);
        if (c == null) {
            System.out.println("[X] Consumer not found: " + id);
            return;
        }
        System.out.println("Found: " + c.getName() + " | Category: " + c.getConnectionType() + " | Meter: " + c.getMeterNumber());
        
        MeterReading latest = meterReadingDAO.getLatestReading(id);
        double suggestedPrev = (latest != null) ? latest.getCurrentReading() : 0.0;

        System.out.print("Enter Billing Month (e.g., 2024-05): ");
        String month = scanner.nextLine().trim();
        if (month.isEmpty()) month = LocalDate.now().toString().substring(0, 7);

        System.out.print("Enter Previous Reading [Default: " + suggestedPrev + "]: ");
        String prevStr = scanner.nextLine().trim();
        double prevReading = prevStr.isEmpty() ? suggestedPrev : Double.parseDouble(prevStr);

        System.out.print("Enter Current Reading: ");
        double currReading = Double.parseDouble(scanner.nextLine().trim());

        if (currReading < prevReading) {
            System.out.println("[X] Validation Error: Current reading (" + currReading + ") cannot be less than Previous (" + prevReading + ")!");
            return;
        }

        double units = currReading - prevReading;
        MeterReading reading = new MeterReading(0, id, month, LocalDate.now(), prevReading, currReading, units);
        meterReadingDAO.recordReading(reading);

        System.out.println("\n[✓] Reading recorded successfully!");
        System.out.printf("    Previous Reading : %10.2f kWh%n", prevReading);
        System.out.printf("    Current Reading  : %10.2f kWh%n", currReading);
        System.out.printf("    Units Consumed   : %10.2f kWh (Current - Previous)%n", units);
    }

    private static void handleGenerateBill() {
        System.out.println("\n--- [Calculate & Generate Electricity Bill] ---");
        System.out.print("Enter Consumer ID: ");
        String id = scanner.nextLine().trim();

        List<MeterReading> readings = meterReadingDAO.getReadingsByConsumer(id);
        if (readings.isEmpty()) {
            System.out.println("[X] No meter readings recorded for consumer " + id);
            return;
        }

        System.out.println("Available readings for " + id + ":");
        for (MeterReading r : readings) {
            System.out.printf(" Reading #%-4d | Month: %s | Units: %8.2f kWh%n", 
                    r.getReadingId(), r.getBillingMonth(), r.getUnitsConsumed());
        }

        System.out.print("Enter Reading ID to Bill: ");
        int readingId = Integer.parseInt(scanner.nextLine().trim());

        try {
            Bill bill = billingService.generateBillForReading(readingId);
            System.out.println("\n[✓] Bill Generated Successfully!");
            System.out.printf(" Bill ID       : %s%n", bill.getBillId());
            System.out.printf(" Units Consumed: %.2f kWh%n", bill.getUnitsConsumed());
            System.out.printf(" Energy Charges: Rs. %.2f%n", bill.getEnergyCharges());
            System.out.printf(" Fixed Charges : Rs. %.2f%n", bill.getFixedCharges());
            System.out.printf(" Taxes (Duty)  : Rs. %.2f%n", bill.getTaxAmount());
            System.out.printf(" Total Payable : Rs. %.2f%n", bill.getTotalAmount());
            System.out.printf(" Due Date      : %s%n", bill.getDueDate());
        } catch (Exception e) {
            System.out.println("[X] Failed to generate bill: " + e.getMessage());
        }
    }

    private static void handleViewConsumerBill() {
        System.out.print("\nEnter Consumer ID (e.g. C101): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Billing Month (leave blank for latest): ");
        String month = scanner.nextLine().trim();
        if (month.isEmpty()) month = null;

        reportService.printConsumerBill(id, month);
    }

    private static void handleMonthlyConsumptionReport() {
        System.out.print("\nEnter Billing Month (e.g. 2024-04 or press ENTER for all months): ");
        String month = scanner.nextLine().trim();
        reportService.printMonthlyConsumptionReport(month.isEmpty() ? null : month);
    }

    private static void handleHighConsumptionConsumers() {
        System.out.print("\nEnter High Consumption Threshold in Units [Default 500]: ");
        String input = scanner.nextLine().trim();
        double threshold = input.isEmpty() ? 500.0 : Double.parseDouble(input);
        reportService.printHighConsumptionReport(threshold);
    }

    private static void handleViewTariffSlabs() {
        System.out.println("\n==========================================================================");
        System.out.println("                       PREDEFINED TARIFF RATE CARDS                       ");
        System.out.println("==========================================================================");
        System.out.println(" 1. Domestic Category:");
        System.out.println("    - 0 to 100 units       : Rs. 3.50 / unit | Fixed: Rs.  50.00 | Tax: 5%");
        System.out.println("    - 101 to 300 units     : Rs. 5.20 / unit | Fixed: Rs.  75.00 | Tax: 5%");
        System.out.println("    - 301 to 500 units     : Rs. 7.50 / unit | Fixed: Rs. 100.00 | Tax: 5%");
        System.out.println("    - Above 500 units      : Rs. 9.80 / unit | Fixed: Rs. 150.00 | Tax: 5%");
        System.out.println("\n 2. Commercial Category:");
        System.out.println("    - 0 to 200 units       : Rs. 8.50 / unit | Fixed: Rs. 200.00 | Tax: 9%");
        System.out.println("    - 201 to 750 units     : Rs. 11.00 / unit | Fixed: Rs. 350.00 | Tax: 9%");
        System.out.println("    - Above 750 units      : Rs. 13.50 / unit | Fixed: Rs. 500.00 | Tax: 9%");
        System.out.println("\n 3. Industrial Category:");
        System.out.println("    - 0 to 1000 units      : Rs. 9.00 / unit | Fixed: Rs. 800.00 | Tax: 12%");
        System.out.println("    - Above 1000 units     : Rs. 12.50 / unit | Fixed: Rs. 1500.00 | Tax: 12%");
        System.out.println("\n 4. Agricultural Category:");
        System.out.println("    - 0 to 500 units       : Rs. 1.50 / unit | Fixed: Rs.  30.00 | Tax: 2%");
        System.out.println("    - Above 500 units      : Rs. 2.80 / unit | Fixed: Rs.  50.00 | Tax: 2%");
        System.out.println("==========================================================================");
    }

    private static void handleExportData() {
        String path = "../database/energy_data.csv";
        reportService.exportDataToCSV(path);
    }
}
