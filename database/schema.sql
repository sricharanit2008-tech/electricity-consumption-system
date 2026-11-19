-- ============================================================================
-- Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
-- Database Schema: schema.sql (Compatible with MySQL and SQLite)
-- ============================================================================

-- Drop tables if they already exist (in reverse order of foreign key dependencies)
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS meter_readings;
DROP TABLE IF EXISTS tariff_slabs;
DROP TABLE IF EXISTS consumers;

-- 1. Consumers Table
-- Stores consumer master information
CREATE TABLE consumers (
    consumer_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    connection_type VARCHAR(20) NOT NULL CHECK (connection_type IN ('Domestic', 'Commercial', 'Industrial', 'Agricultural')),
    meter_number VARCHAR(30) UNIQUE NOT NULL,
    connection_date DATE NOT NULL,
    status VARCHAR(15) DEFAULT 'Active' CHECK (status IN ('Active', 'Suspended', 'Disconnected'))
);

-- 2. Tariff Slabs Table
-- Stores slab-wise energy charges, fixed charges, and tax percentages based on connection type
CREATE TABLE tariff_slabs (
    slab_id INT PRIMARY KEY,
    connection_type VARCHAR(20) NOT NULL,
    min_units INT NOT NULL,
    max_units INT NOT NULL, -- Use a high number like 999999 for open-ended slabs
    rate_per_unit DECIMAL(6, 2) NOT NULL,
    fixed_charge DECIMAL(8, 2) NOT NULL,
    tax_percent DECIMAL(5, 2) NOT NULL DEFAULT 5.00
);

-- 3. Meter Readings Table
-- Records previous and current meter readings, calculating units consumed
CREATE TABLE meter_readings (
    reading_id INT PRIMARY KEY,
    consumer_id VARCHAR(20) NOT NULL,
    billing_month VARCHAR(7) NOT NULL, -- Format: 'YYYY-MM' (e.g., '2024-01')
    reading_date DATE NOT NULL,
    previous_reading DECIMAL(10, 2) NOT NULL,
    current_reading DECIMAL(10, 2) NOT NULL,
    units_consumed DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_meter_consumer FOREIGN KEY (consumer_id) REFERENCES consumers (consumer_id) ON DELETE CASCADE,
    CONSTRAINT chk_readings CHECK (current_reading >= previous_reading)
);

-- 4. Bills Table
-- Stores computed electricity bills with breakdowns of energy, fixed charges, and taxes
CREATE TABLE bills (
    bill_id VARCHAR(25) PRIMARY KEY,
    reading_id INT NOT NULL,
    consumer_id VARCHAR(20) NOT NULL,
    billing_month VARCHAR(7) NOT NULL,
    units_consumed DECIMAL(10, 2) NOT NULL,
    energy_charges DECIMAL(10, 2) NOT NULL,
    fixed_charges DECIMAL(8, 2) NOT NULL,
    tax_amount DECIMAL(8, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    bill_date DATE NOT NULL,
    due_date DATE NOT NULL,
    payment_status VARCHAR(15) DEFAULT 'Unpaid' CHECK (payment_status IN ('Unpaid', 'Paid', 'Overdue')),
    CONSTRAINT fk_bill_reading FOREIGN KEY (reading_id) REFERENCES meter_readings (reading_id) ON DELETE CASCADE,
    CONSTRAINT fk_bill_consumer FOREIGN KEY (consumer_id) REFERENCES consumers (consumer_id) ON DELETE CASCADE
);

-- Indexes for performance on frequent lookups
CREATE INDEX idx_reading_consumer ON meter_readings(consumer_id);
CREATE INDEX idx_reading_month ON meter_readings(billing_month);
CREATE INDEX idx_bills_consumer ON bills(consumer_id);
CREATE INDEX idx_bills_month ON bills(billing_month);
