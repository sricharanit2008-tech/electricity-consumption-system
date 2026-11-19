-- ============================================================================
-- Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
-- SQL Analytical and Operational Queries: queries.sql
-- ============================================================================

-- 1. Retrieve all registered consumers with their connection types and meter numbers
SELECT 
    consumer_id, 
    name, 
    address, 
    phone, 
    connection_type, 
    meter_number, 
    status 
FROM consumers 
ORDER BY connection_type, consumer_id;


-- 2. Calculate electricity consumption by subtracting previous reading from current reading
SELECT 
    mr.reading_id,
    c.consumer_id,
    c.name,
    c.connection_type,
    mr.billing_month,
    mr.previous_reading,
    mr.current_reading,
    (mr.current_reading - mr.previous_reading) AS calculated_consumption_units,
    mr.units_consumed AS recorded_units
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
ORDER BY mr.billing_month DESC, mr.consumer_id;


-- 3. Consumer-wise detailed bill lookup
SELECT 
    b.bill_id,
    c.consumer_id,
    c.name,
    c.connection_type,
    c.address,
    b.billing_month,
    b.units_consumed,
    b.energy_charges,
    b.fixed_charges,
    b.tax_amount,
    b.total_amount,
    b.due_date,
    b.payment_status
FROM bills b
JOIN consumers c ON b.consumer_id = c.consumer_id
WHERE c.consumer_id = 'C101'
ORDER BY b.billing_month DESC;


-- 4. Monthly consumption report across all consumers and connection categories
SELECT 
    billing_month,
    COUNT(DISTINCT consumer_id) AS total_active_meters,
    ROUND(SUM(units_consumed), 2) AS total_units_consumed,
    ROUND(AVG(units_consumed), 2) AS avg_units_per_consumer,
    ROUND(MIN(units_consumed), 2) AS min_units_consumed,
    ROUND(MAX(units_consumed), 2) AS max_units_consumed
FROM meter_readings
GROUP BY billing_month
ORDER BY billing_month;


-- 5. Monthly consumption breakdown by connection type
SELECT 
    mr.billing_month,
    c.connection_type,
    COUNT(c.consumer_id) AS consumer_count,
    ROUND(SUM(mr.units_consumed), 2) AS total_units,
    ROUND(AVG(mr.units_consumed), 2) AS average_units
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
GROUP BY mr.billing_month, c.connection_type
ORDER BY mr.billing_month, total_units DESC;


-- 6. Identify High-Consumption Consumers (Threshold: Units > 500)
SELECT 
    mr.billing_month,
    c.consumer_id,
    c.name,
    c.connection_type,
    mr.units_consumed,
    CASE 
        WHEN mr.units_consumed >= 3000 THEN 'Critical High (Industrial)'
        WHEN mr.units_consumed >= 1000 THEN 'Very High'
        WHEN mr.units_consumed >= 500  THEN 'High'
        ELSE 'Normal'
    END AS consumption_severity
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
WHERE mr.units_consumed >= 500
ORDER BY mr.units_consumed DESC;


-- 7. High-Consumption Consumers relative to connection-type average (Outlier Detection)
WITH CategoryAvg AS (
    SELECT 
        c.connection_type,
        AVG(mr.units_consumed) AS cat_avg_units
    FROM meter_readings mr
    JOIN consumers c ON mr.consumer_id = c.consumer_id
    GROUP BY c.connection_type
)
SELECT 
    c.consumer_id,
    c.name,
    c.connection_type,
    ROUND(mr.units_consumed, 2) AS consumer_units,
    ROUND(ca.cat_avg_units, 2) AS category_average,
    ROUND((mr.units_consumed - ca.cat_avg_units), 2) AS excess_units,
    ROUND(((mr.units_consumed - ca.cat_avg_units) / ca.cat_avg_units) * 100, 2) AS percentage_above_average
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
JOIN CategoryAvg ca ON c.connection_type = ca.connection_type
WHERE mr.units_consumed > ca.cat_avg_units
ORDER BY percentage_above_average DESC;


-- 8. Billing and Revenue Summary Report
SELECT 
    c.connection_type,
    COUNT(b.bill_id) AS total_bills_issued,
    ROUND(SUM(b.units_consumed), 2) AS total_units_billed,
    ROUND(SUM(b.energy_charges), 2) AS total_energy_revenue,
    ROUND(SUM(b.fixed_charges), 2) AS total_fixed_revenue,
    ROUND(SUM(b.tax_amount), 2) AS total_tax_collected,
    ROUND(SUM(b.total_amount), 2) AS gross_total_revenue,
    SUM(CASE WHEN b.payment_status = 'Paid' THEN b.total_amount ELSE 0 END) AS collected_revenue,
    SUM(CASE WHEN b.payment_status = 'Unpaid' THEN b.total_amount ELSE 0 END) AS pending_revenue
FROM bills b
JOIN consumers c ON b.consumer_id = c.consumer_id
GROUP BY c.connection_type
ORDER BY gross_total_revenue DESC;
