-- ============================================================================
-- Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
-- Sample Data: sample_data.sql
-- ============================================================================

-- 1. Insert Tariff Slabs
-- Domestic: Progressive tiered rates
INSERT INTO tariff_slabs (slab_id, connection_type, min_units, max_units, rate_per_unit, fixed_charge, tax_percent) VALUES
(1, 'Domestic', 0, 100, 3.50, 50.00, 5.00),
(2, 'Domestic', 101, 300, 5.20, 75.00, 5.00),
(3, 'Domestic', 301, 500, 7.50, 100.00, 5.00),
(4, 'Domestic', 501, 999999, 9.80, 150.00, 5.00),

-- Commercial: Higher base rate
(5, 'Commercial', 0, 200, 8.50, 200.00, 9.00),
(6, 'Commercial', 201, 750, 11.00, 350.00, 9.00),
(7, 'Commercial', 751, 999999, 13.50, 500.00, 9.00),

-- Industrial: High-power heavy usage rates
(8, 'Industrial', 0, 1000, 9.00, 800.00, 12.00),
(9, 'Industrial', 1001, 999999, 12.50, 1500.00, 12.00),

-- Agricultural: Subsidized rates
(10, 'Agricultural', 0, 500, 1.50, 30.00, 2.00),
(11, 'Agricultural', 501, 999999, 2.80, 50.00, 2.00);


-- 2. Insert Consumers
INSERT INTO consumers (consumer_id, name, address, phone, connection_type, meter_number, connection_date, status) VALUES
('C101', 'Aarav Sharma', '14 Maple Residency, Green Park', '9876543210', 'Domestic', 'MTR-DOM-101', '2022-01-15', 'Active'),
('C102', 'Priya Patel', '22 Sunset Boulevard, Sector 4', '9812345678', 'Domestic', 'MTR-DOM-102', '2022-03-20', 'Active'),
('C103', 'Rajesh Verma', '88 Hill Road, Civil Lines', '9723456789', 'Domestic', 'MTR-DOM-103', '2021-11-10', 'Active'),
('C104', 'Ananya Gupta', '104 Lakeview Apartments', '9934567890', 'Domestic', 'MTR-DOM-104', '2023-05-12', 'Active'),
('C105', 'Vikram Singh', '45 Royal Enclave', '9845678901', 'Domestic', 'MTR-DOM-105', '2020-08-01', 'Active'),

('C201', 'Apex Tech Solutions Hub', 'IT Tech Park, Block C', '9856789012', 'Commercial', 'MTR-COM-201', '2021-02-14', 'Active'),
('C202', 'Metro Central Supermarket', 'Market Square, Main Road', '9867890123', 'Commercial', 'MTR-COM-202', '2022-06-18', 'Active'),
('C203', 'Grand Cafe & Bakers', 'Corner Street, Downtown', '9878901234', 'Commercial', 'MTR-COM-203', '2023-01-05', 'Active'),

('C301', 'Falcon Heavy Steels Ltd', 'Industrial Area, Phase 1', '9889012345', 'Industrial', 'MTR-IND-301', '2019-04-10', 'Active'),
('C302', 'Sunrise Textile Mills', 'MIDC Zone, Plot 5', '9890123456', 'Industrial', 'MTR-IND-302', '2020-09-22', 'Active'),

('C401', 'Kisan Agro Cooperative', 'Village Ramnagar, Farm 12', '9901234567', 'Agricultural', 'MTR-AGR-401', '2021-07-30', 'Active'),
('C402', 'Greenfield Dairy Farms', 'National Highway 48', '9912345678', 'Agricultural', 'MTR-AGR-402', '2022-10-15', 'Active');


-- 3. Insert Meter Readings (2024-01 to 2024-04)
-- Consumer C101 (Domestic - Average)
INSERT INTO meter_readings VALUES (1, 'C101', '2024-01', '2024-01-31', 1200.00, 1340.00, 140.00);
INSERT INTO meter_readings VALUES (2, 'C101', '2024-02', '2024-02-29', 1340.00, 1505.00, 165.00);
INSERT INTO meter_readings VALUES (3, 'C101', '2024-03', '2024-03-31', 1505.00, 1725.00, 220.00);
INSERT INTO meter_readings VALUES (4, 'C101', '2024-04', '2024-04-30', 1725.00, 1995.00, 270.00);

-- Consumer C102 (Domestic - Low/Moderate)
INSERT INTO meter_readings VALUES (5, 'C102', '2024-01', '2024-01-31', 850.00, 930.00, 80.00);
INSERT INTO meter_readings VALUES (6, 'C102', '2024-02', '2024-02-29', 930.00, 1025.00, 95.00);
INSERT INTO meter_readings VALUES (7, 'C102', '2024-03', '2024-03-31', 1025.00, 1145.00, 120.00);
INSERT INTO meter_readings VALUES (8, 'C102', '2024-04', '2024-04-30', 1145.00, 1290.00, 145.00);

-- Consumer C103 (Domestic - High Domestic with AC in Summer)
INSERT INTO meter_readings VALUES (9, 'C103', '2024-01', '2024-01-31', 2100.00, 2380.00, 280.00);
INSERT INTO meter_readings VALUES (10, 'C103', '2024-02', '2024-02-29', 2380.00, 2720.00, 340.00);
INSERT INTO meter_readings VALUES (11, 'C103', '2024-03', '2024-03-31', 2720.00, 3260.00, 540.00);
INSERT INTO meter_readings VALUES (12, 'C103', '2024-04', '2024-04-30', 3260.00, 3910.00, 650.00);

-- Consumer C104 (Domestic)
INSERT INTO meter_readings VALUES (13, 'C104', '2024-01', '2024-01-31', 400.00, 510.00, 110.00);
INSERT INTO meter_readings VALUES (14, 'C104', '2024-02', '2024-02-29', 510.00, 640.00, 130.00);
INSERT INTO meter_readings VALUES (15, 'C104', '2024-03', '2024-03-31', 640.00, 810.00, 170.00);
INSERT INTO meter_readings VALUES (16, 'C104', '2024-04', '2024-04-30', 810.00, 1030.00, 220.00);

-- Consumer C105 (Domestic)
INSERT INTO meter_readings VALUES (17, 'C105', '2024-01', '2024-01-31', 3400.00, 3590.00, 190.00);
INSERT INTO meter_readings VALUES (18, 'C105', '2024-02', '2024-02-29', 3590.00, 3800.00, 210.00);
INSERT INTO meter_readings VALUES (19, 'C105', '2024-03', '2024-03-31', 3800.00, 4080.00, 280.00);
INSERT INTO meter_readings VALUES (20, 'C105', '2024-04', '2024-04-30', 4080.00, 4440.00, 360.00);

-- Consumer C201 (Commercial - IT Hub)
INSERT INTO meter_readings VALUES (21, 'C201', '2024-01', '2024-01-31', 12500.00, 13350.00, 850.00);
INSERT INTO meter_readings VALUES (22, 'C201', '2024-02', '2024-02-29', 13350.00, 14280.00, 930.00);
INSERT INTO meter_readings VALUES (23, 'C201', '2024-03', '2024-03-31', 14280.00, 15380.00, 1100.00);
INSERT INTO meter_readings VALUES (24, 'C201', '2024-04', '2024-04-30', 15380.00, 16640.00, 1260.00);

-- Consumer C202 (Commercial - Supermarket)
INSERT INTO meter_readings VALUES (25, 'C202', '2024-01', '2024-01-31', 8900.00, 9550.00, 650.00);
INSERT INTO meter_readings VALUES (26, 'C202', '2024-02', '2024-02-29', 9550.00, 10240.00, 690.00);
INSERT INTO meter_readings VALUES (27, 'C202', '2024-03', '2024-03-31', 10240.00, 11020.00, 780.00);
INSERT INTO meter_readings VALUES (28, 'C202', '2024-04', '2024-04-30', 11020.00, 11910.00, 890.00);

-- Consumer C203 (Commercial - Cafe)
INSERT INTO meter_readings VALUES (29, 'C203', '2024-01', '2024-01-31', 4500.00, 4820.00, 320.00);
INSERT INTO meter_readings VALUES (30, 'C203', '2024-02', '2024-02-29', 4820.00, 5170.00, 350.00);
INSERT INTO meter_readings VALUES (31, 'C203', '2024-03', '2024-03-31', 5170.00, 5580.00, 410.00);
INSERT INTO meter_readings VALUES (32, 'C203', '2024-04', '2024-04-30', 5580.00, 6060.00, 480.00);

-- Consumer C301 (Industrial - Steel Plant - Heavy High Consumption)
INSERT INTO meter_readings VALUES (33, 'C301', '2024-01', '2024-01-31', 45000.00, 49200.00, 4200.00);
INSERT INTO meter_readings VALUES (34, 'C301', '2024-02', '2024-02-29', 49200.00, 53800.00, 4600.00);
INSERT INTO meter_readings VALUES (35, 'C301', '2024-03', '2024-03-31', 53800.00, 59100.00, 5300.00);
INSERT INTO meter_readings VALUES (36, 'C301', '2024-04', '2024-04-30', 59100.00, 64950.00, 5850.00);

-- Consumer C302 (Industrial - Textile Mill)
INSERT INTO meter_readings VALUES (37, 'C302', '2024-01', '2024-01-31', 32000.00, 34800.00, 2800.00);
INSERT INTO meter_readings VALUES (38, 'C302', '2024-02', '2024-02-29', 34800.00, 37750.00, 2950.00);
INSERT INTO meter_readings VALUES (39, 'C302', '2024-03', '2024-03-31', 37750.00, 40950.00, 3200.00);
INSERT INTO meter_readings VALUES (40, 'C302', '2024-04', '2024-04-30', 40950.00, 44400.00, 3450.00);

-- Consumer C401 (Agricultural)
INSERT INTO meter_readings VALUES (41, 'C401', '2024-01', '2024-01-31', 6100.00, 6450.00, 350.00);
INSERT INTO meter_readings VALUES (42, 'C401', '2024-02', '2024-02-29', 6450.00, 6880.00, 430.00);
INSERT INTO meter_readings VALUES (43, 'C401', '2024-03', '2024-03-31', 6880.00, 7420.00, 540.00);
INSERT INTO meter_readings VALUES (44, 'C401', '2024-04', '2024-04-30', 7420.00, 8080.00, 660.00);

-- Consumer C402 (Agricultural)
INSERT INTO meter_readings VALUES (45, 'C402', '2024-01', '2024-01-31', 4800.00, 5080.00, 280.00);
INSERT INTO meter_readings VALUES (46, 'C402', '2024-02', '2024-02-29', 5080.00, 5400.00, 320.00);
INSERT INTO meter_readings VALUES (47, 'C402', '2024-03', '2024-03-31', 5400.00, 5780.00, 380.00);
INSERT INTO meter_readings VALUES (48, 'C402', '2024-04', '2024-04-30', 5780.00, 6230.00, 450.00);


-- 4. Insert Sample Bills (Computed based on tariff slabs)
-- Domestic C101: Jan (140 units -> (100*3.50 + 40*5.20)=558.00 + 75.00 fixed = 633.00 + 5% tax(31.65) = 664.65)
INSERT INTO bills VALUES ('BILL-202401-C101', 1, 'C101', '2024-01', 140.00, 558.00, 75.00, 31.65, 664.65, '2024-02-05', '2024-02-20', 'Paid');
INSERT INTO bills VALUES ('BILL-202402-C101', 2, 'C101', '2024-02', 165.00, 688.00, 75.00, 38.15, 801.15, '2024-03-05', '2024-03-20', 'Paid');
INSERT INTO bills VALUES ('BILL-202403-C101', 3, 'C101', '2024-03', 220.00, 974.00, 75.00, 52.45, 1101.45, '2024-04-05', '2024-04-20', 'Paid');
INSERT INTO bills VALUES ('BILL-202404-C101', 4, 'C101', '2024-04', 270.00, 1234.00, 75.00, 65.45, 1374.45, '2024-05-05', '2024-05-20', 'Unpaid');

-- Domestic C103 (High Domestic): Jan (280 units) & Apr (650 units)
INSERT INTO bills VALUES ('BILL-202401-C103', 9, 'C103', '2024-01', 280.00, 1286.00, 75.00, 68.05, 1429.05, '2024-02-05', '2024-02-20', 'Paid');
INSERT INTO bills VALUES ('BILL-202404-C103', 12, 'C103', '2024-04', 650.00, 4360.00, 150.00, 225.50, 4735.50, '2024-05-05', '2024-05-20', 'Unpaid');

-- Commercial C201 (IT Hub): Jan (850 units) & Apr (1260 units)
INSERT INTO bills VALUES ('BILL-202401-C201', 21, 'C201', '2024-01', 850.00, 9100.00, 500.00, 864.00, 10464.00, '2024-02-05', '2024-02-20', 'Paid');
INSERT INTO bills VALUES ('BILL-202404-C201', 24, 'C201', '2024-04', 1260.00, 14635.00, 500.00, 1362.15, 16497.15, '2024-05-05', '2024-05-20', 'Unpaid');

-- Industrial C301 (Steel Plant - High usage): Jan (4200 units) & Apr (5850 units)
INSERT INTO bills VALUES ('BILL-202401-C301', 33, 'C301', '2024-01', 4200.00, 49000.00, 1500.00, 6060.00, 56560.00, '2024-02-05', '2024-02-20', 'Paid');
INSERT INTO bills VALUES ('BILL-202404-C301', 36, 'C301', '2024-04', 5850.00, 69625.00, 1500.00, 8535.00, 79660.00, '2024-05-05', '2024-05-20', 'Unpaid');

-- Agricultural C401: Jan (350 units) & Apr (660 units)
INSERT INTO bills VALUES ('BILL-202401-C401', 41, 'C401', '2024-01', 350.00, 525.00, 30.00, 11.10, 566.10, '2024-02-05', '2024-02-20', 'Paid');
INSERT INTO bills VALUES ('BILL-202404-C401', 44, 'C401', '2024-04', 660.00, 1198.00, 50.00, 24.96, 1272.96, '2024-05-05', '2024-05-20', 'Unpaid');
