#!/usr/bin/env python3
"""
Database Initialization Script
Executes schema.sql and sample_data.sql to generate database.db (SQLite)
and exports energy_data.csv for immediate analysis.
"""

import sqlite3
import os
import csv

DB_PATH = os.path.join(os.path.dirname(__file__), 'electricity.db')
SCHEMA_PATH = os.path.join(os.path.dirname(__file__), 'schema.sql')
SAMPLE_DATA_PATH = os.path.join(os.path.dirname(__file__), 'sample_data.sql')
CSV_EXPORT_PATH = os.path.join(os.path.dirname(__file__), 'energy_data.csv')

def init_db():
    print(f"[*] Initializing SQLite Database at: {DB_PATH}")
    
    # Remove old database if exists
    if os.path.exists(DB_PATH):
        try:
            os.remove(DB_PATH)
        except Exception:
            pass

    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    # Read and run schema
    with open(SCHEMA_PATH, 'r', encoding='utf-8') as f:
        schema_sql = f.read()
    cursor.executescript(schema_sql)
    print(" [+] schema.sql executed successfully.")

    # Read and run sample data
    with open(SAMPLE_DATA_PATH, 'r', encoding='utf-8') as f:
        data_sql = f.read()
    cursor.executescript(data_sql)
    print(" [+] sample_data.sql executed successfully.")

    conn.commit()

    # Export merged analytical dataset to CSV
    export_query = """
    SELECT 
        c.consumer_id,
        c.name,
        c.connection_type,
        c.address,
        c.meter_number,
        mr.reading_id,
        mr.billing_month,
        mr.reading_date,
        mr.previous_reading,
        mr.current_reading,
        mr.units_consumed,
        COALESCE(b.energy_charges, 0.0) AS energy_charges,
        COALESCE(b.fixed_charges, 0.0) AS fixed_charges,
        COALESCE(b.tax_amount, 0.0) AS tax_amount,
        COALESCE(b.total_amount, 0.0) AS bill_total,
        COALESCE(b.payment_status, 'Unbilled') AS payment_status
    FROM meter_readings mr
    JOIN consumers c ON mr.consumer_id = c.consumer_id
    LEFT JOIN bills b ON mr.reading_id = b.reading_id
    ORDER BY mr.billing_month, c.connection_type, mr.units_consumed DESC;
    """
    
    cursor.execute(export_query)
    rows = cursor.fetchall()
    headers = [description[0] for description in cursor.description]

    with open(CSV_EXPORT_PATH, 'w', newline='', encoding='utf-8') as csv_file:
        writer = csv.writer(csv_file)
        writer.writerow(headers)
        writer.writerows(rows)

    print(f" [+] Exported {len(rows)} records to {CSV_EXPORT_PATH}")
    conn.close()
    print(" [*] Database setup completed successfully.")

if __name__ == '__main__':
    init_db()
