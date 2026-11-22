#!/usr/bin/env python3
"""
===============================================================================
Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
Student Register Number: 732925ITR104
Component: DBMS Module Local Terminal Query Runner
===============================================================================
Executes SQL queries directly on electricity.db and prints formatted terminal outputs.
Zero external dependencies required (uses built-in sqlite3).
"""

import os
import sys
import sqlite3

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
DB_PATH = os.path.join(SCRIPT_DIR, "electricity.db")
SCHEMA_PATH = os.path.join(SCRIPT_DIR, "schema.sql")
SAMPLE_DATA_PATH = os.path.join(SCRIPT_DIR, "sample_data.sql")

def ensure_database():
    """Initializes the database if not present."""
    if not os.path.exists(DB_PATH):
        print(f"[*] Initializing SQLite Database at: {DB_PATH}")
        conn = sqlite3.connect(DB_PATH)
        cur = conn.cursor()
        with open(SCHEMA_PATH, "r", encoding="utf-8") as f:
            cur.executescript(f.read())
        with open(SAMPLE_DATA_PATH, "r", encoding="utf-8") as f:
            cur.executescript(f.read())
        conn.commit()
        conn.close()
        print("[✓] Database initialized successfully with schema and sample data.")

def print_table(cursor, max_rows=50):
    """Prints cursor result set as a clean formatted ASCII table."""
    headers = [desc[0] for desc in cursor.description]
    rows = cursor.fetchall()
    
    if not rows:
        print("  (0 rows returned)")
        return

    # Calculate column widths
    col_widths = [len(h) for h in headers]
    for row in rows[:max_rows]:
        for idx, val in enumerate(row):
            sval = str(val) if val is not None else "NULL"
            if len(sval) > col_widths[idx]:
                col_widths[idx] = len(sval)

    # Format separator and header
    sep = "+-" + "-+-".join("-" * w for w in col_widths) + "-+"
    header_str = "| " + " | ".join(h.ljust(col_widths[i]) for i, h in enumerate(headers)) + " |"

    print(sep)
    print(header_str)
    print(sep)

    for row in rows[:max_rows]:
        row_str = []
        for i, val in enumerate(row):
            sval = str(val) if val is not None else "NULL"
            # Right-align numeric values, left-align text
            if isinstance(val, (int, float)):
                row_str.append(sval.rjust(col_widths[i]))
            else:
                row_str.append(sval.ljust(col_widths[i]))
        print("| " + " | ".join(row_str) + " |")

    print(sep)
    print(f" Total Rows: {len(rows)}" + (f" (Showing first {max_rows})" if len(rows) > max_rows else ""))

def run_query(conn, title, sql, params=()):
    print("\n" + "="*80)
    print(f"  📌 {title}")
    print("="*80)
    print(" SQL Query Executed:")
    for line in sql.strip().splitlines():
        print(f"   {line}")
    print("-" * 80)
    
    cur = conn.cursor()
    try:
        cur.execute(sql, params)
        print_table(cur)
    except Exception as e:
        print(f"[X] Query execution error: {e}")
    print("="*80)

def main():
    ensure_database()
    conn = sqlite3.connect(DB_PATH)

    while True:
        print("\n" + "="*80)
        print("  ⚡ DBMS MODULE: ELECTRICITY CONSUMPTION SQL QUERY RUNNER & AUDIT TERMINAL")
        print("                 Scenario 14 | Register No.: 732925ITR104")
        print("================================================================================")
        print(" 1. Query 1: View Registered Consumers & Meter Specs                (FR1)")
        print(" 2. Query 2: Compute Consumption (Current - Previous Reading)       (FR2, FR3)")
        print(" 3. Query 3: Consumer-Wise Detailed Bill & Invoice Lookup           (FR5)")
        print(" 4. Query 4: Monthly Consumption Report with Aggregations (SUM/AVG) (FR6)")
        print(" 5. Query 5: Sector Breakdown & Energy Share by Connection Type     (FR6)")
        print(" 6. Query 6: Identify High-Consumption Consumers (Threshold >= 500) (FR7)")
        print(" 7. Query 7: High-Consumption Outlier Detection (% Above Cat Mean)  (FR7)")
        print(" 8. Query 8: Gross Revenue & Payment Realization Summary            (FR4, FR5)")
        print(" 9. Query 9: View Active Tariff Slabs & Progressive Rates           (FR4)")
        print(" 10. Run All Queries Sequentially (Complete Professor Evaluation Mode)")
        print(" 0. Exit DBMS Runner")
        print("--------------------------------------------------------------------------------")
        choice = input(" Enter your choice (0-10): ").strip()

        if choice == "1":
            q = "SELECT consumer_id, name, address, phone, connection_type, meter_number, status FROM consumers ORDER BY connection_type, consumer_id;"
            run_query(conn, "QUERY 1: Registered Consumers & Meter Specifications", q)

        elif choice == "2":
            q = """SELECT 
    mr.reading_id,
    c.consumer_id,
    c.name,
    c.connection_type,
    mr.billing_month,
    mr.previous_reading,
    mr.current_reading,
    ROUND(mr.current_reading - mr.previous_reading, 2) AS calculated_consumption_units,
    mr.units_consumed AS recorded_units
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
ORDER BY mr.billing_month DESC, mr.consumer_id
LIMIT 12;"""
            run_query(conn, "QUERY 2: Consumption Calculation (Current - Previous Reading)", q)

        elif choice == "3":
            cid = input(" Enter Consumer ID [Default: C101]: ").strip().upper()
            if not cid: cid = "C101"
            q = """SELECT 
    b.bill_id,
    c.consumer_id,
    c.name,
    c.connection_type,
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
WHERE c.consumer_id = ?
ORDER BY b.billing_month DESC;"""
            run_query(conn, f"QUERY 3: Detailed Bill Ledger for Consumer {cid}", q, (cid,))

        elif choice == "4":
            q = """SELECT 
    billing_month,
    COUNT(DISTINCT consumer_id) AS active_meters,
    ROUND(SUM(units_consumed), 2) AS total_units_kwh,
    ROUND(AVG(units_consumed), 2) AS avg_units_per_meter,
    ROUND(MIN(units_consumed), 2) AS min_units_kwh,
    ROUND(MAX(units_consumed), 2) AS peak_units_kwh
FROM meter_readings
GROUP BY billing_month
ORDER BY billing_month;"""
            run_query(conn, "QUERY 4: Monthly Consumption Report with Statistical Aggregates", q)

        elif choice == "5":
            q = """SELECT 
    mr.billing_month,
    c.connection_type,
    COUNT(c.consumer_id) AS consumer_count,
    ROUND(SUM(mr.units_consumed), 2) AS total_units_kwh,
    ROUND(AVG(mr.units_consumed), 2) AS average_units_kwh
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
GROUP BY mr.billing_month, c.connection_type
ORDER BY mr.billing_month, total_units_kwh DESC;"""
            run_query(conn, "QUERY 5: Monthly Consumption Breakdown by Connection Type", q)

        elif choice == "6":
            q = """SELECT 
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
ORDER BY mr.units_consumed DESC
LIMIT 12;"""
            run_query(conn, "QUERY 6: High-Consumption Outliers (Threshold >= 500 kWh)", q)

        elif choice == "7":
            q = """WITH CategoryAvg AS (
    SELECT c.connection_type, AVG(mr.units_consumed) AS cat_avg_units
    FROM meter_readings mr
    JOIN consumers c ON mr.consumer_id = c.consumer_id
    GROUP BY c.connection_type
)
SELECT 
    c.consumer_id,
    c.name,
    c.connection_type,
    ROUND(mr.units_consumed, 2) AS consumer_units,
    ROUND(ca.cat_avg_units, 2) AS category_avg_units,
    ROUND(mr.units_consumed - ca.cat_avg_units, 2) AS excess_units,
    ROUND(((mr.units_consumed - ca.cat_avg_units) / ca.cat_avg_units) * 100, 2) AS pct_above_avg
FROM meter_readings mr
JOIN consumers c ON mr.consumer_id = c.consumer_id
JOIN CategoryAvg ca ON c.connection_type = ca.connection_type
WHERE mr.units_consumed > ca.cat_avg_units
ORDER BY pct_above_avg DESC
LIMIT 10;"""
            run_query(conn, "QUERY 7: Anomaly Detection (% Deviation Above Category Average)", q)

        elif choice == "8":
            q = """SELECT 
    c.connection_type,
    COUNT(b.bill_id) AS bills_issued,
    ROUND(SUM(b.units_consumed), 2) AS total_units_billed,
    ROUND(SUM(b.energy_charges), 2) AS energy_revenue,
    ROUND(SUM(b.fixed_charges), 2) AS fixed_revenue,
    ROUND(SUM(b.tax_amount), 2) AS tax_collected,
    ROUND(SUM(b.total_amount), 2) AS gross_revenue,
    ROUND(SUM(CASE WHEN b.payment_status = 'Paid' THEN b.total_amount ELSE 0 END), 2) AS collected_revenue,
    ROUND(SUM(CASE WHEN b.payment_status = 'Unpaid' THEN b.total_amount ELSE 0 END), 2) AS pending_revenue
FROM bills b
JOIN consumers c ON b.consumer_id = c.consumer_id
GROUP BY c.connection_type
ORDER BY gross_revenue DESC;"""
            run_query(conn, "QUERY 8: Gross Revenue & Payment Realization by Category", q)

        elif choice == "9":
            q = """SELECT 
    slab_id,
    connection_type,
    min_units,
    CASE WHEN max_units >= 999999 THEN 'Above' ELSE CAST(max_units AS TEXT) END AS max_units,
    rate_per_unit,
    fixed_charge,
    tax_percent
FROM tariff_slabs
ORDER BY connection_type, slab_id;"""
            run_query(conn, "QUERY 9: Predefined Progressive Tariff Slab Structure", q)

        elif choice == "10":
            print("\n" + "#"*80)
            print("  🎓 RUNNING ALL 9 DBMS QUERIES SEQUENTIALLY (PROFESSOR EVALUATION MODE)")
            print("#"*80)
            
            # Query 1
            run_query(conn, "1. Consumers Master Table", "SELECT consumer_id, name, connection_type, meter_number, status FROM consumers LIMIT 6;")
            # Query 2
            run_query(conn, "2. Consumption Telemetry (Current - Previous)", """SELECT mr.reading_id, mr.consumer_id, mr.billing_month, mr.previous_reading, mr.current_reading, 
ROUND(mr.current_reading - mr.previous_reading, 2) AS units_calculated, mr.units_consumed FROM meter_readings mr LIMIT 6;""")
            # Query 3
            run_query(conn, "3. Consumer Invoicing Ledger", "SELECT bill_id, consumer_id, billing_month, units_consumed, energy_charges, fixed_charges, tax_amount, total_amount, payment_status FROM bills WHERE consumer_id='C101';")
            # Query 4
            run_query(conn, "4. Monthly Consumption Aggregates", """SELECT billing_month, COUNT(DISTINCT consumer_id) AS meters, ROUND(SUM(units_consumed), 2) AS total_kwh, 
ROUND(AVG(units_consumed), 2) AS avg_kwh, ROUND(MAX(units_consumed), 2) AS peak_kwh FROM meter_readings GROUP BY billing_month;""")
            # Query 5
            run_query(conn, "5. Sector-wise Monthly Share", """SELECT mr.billing_month, c.connection_type, ROUND(SUM(mr.units_consumed), 2) AS total_kwh, 
ROUND(AVG(mr.units_consumed), 2) AS avg_kwh FROM meter_readings mr JOIN consumers c ON mr.consumer_id = c.consumer_id GROUP BY mr.billing_month, c.connection_type LIMIT 8;""")
            # Query 6
            run_query(conn, "6. High-Consumption Outlier Flags (>= 500 kWh)", """SELECT mr.consumer_id, c.name, c.connection_type, mr.billing_month, mr.units_consumed, 
CASE WHEN mr.units_consumed >= 3000 THEN 'Critical High' WHEN mr.units_consumed >= 1000 THEN 'Very High' ELSE 'High' END AS severity 
FROM meter_readings mr JOIN consumers c ON mr.consumer_id = c.consumer_id WHERE mr.units_consumed >= 500 ORDER BY mr.units_consumed DESC LIMIT 8;""")
            # Query 7
            run_query(conn, "7. Gross Revenue by Connection Type", """SELECT c.connection_type, COUNT(b.bill_id) AS bills, ROUND(SUM(b.total_amount), 2) AS gross_revenue, 
ROUND(SUM(CASE WHEN b.payment_status='Paid' THEN b.total_amount ELSE 0 END), 2) AS collected, 
ROUND(SUM(CASE WHEN b.payment_status='Unpaid' THEN b.total_amount ELSE 0 END), 2) AS pending 
FROM bills b JOIN consumers c ON b.consumer_id = c.consumer_id GROUP BY c.connection_type;""")

        elif choice == "0":
            print("\n[!] Exiting DBMS Query Terminal. Thank you!")
            break
        else:
            print("\n[X] Invalid selection. Please enter a number between 0 and 10.")

        input("\nPress ENTER to continue...")

    conn.close()

if __name__ == "__main__":
    main()
