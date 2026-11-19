#!/usr/bin/env python3
"""
================================================================================
Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
Component: Python Data Retrieval, Preprocessing & Visualization Engine
Functional Requirement 6:
"Retrieve electricity consumption data from the database, preprocess it, 
 and generate visualizations using Python."
================================================================================
"""

import os
import sys
import sqlite3
import csv
import math
from datetime import datetime

# Setup paths
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.abspath(os.path.join(SCRIPT_DIR, '..'))
DB_PATH = os.path.join(PROJECT_ROOT, 'database', 'electricity.db')
CSV_PATH = os.path.join(PROJECT_ROOT, 'database', 'energy_data.csv')
OUTPUT_DIR = os.path.join(SCRIPT_DIR, 'output')
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Check for data science libraries
try:
    import pandas as pd
    import numpy as np
    HAS_PANDAS = True
except ImportError:
    HAS_PANDAS = False

try:
    import matplotlib
    matplotlib.use('Agg') # Headless backend
    import matplotlib.pyplot as plt
    HAS_MATPLOTLIB = True
except ImportError:
    HAS_MATPLOTLIB = False

print(f"[*] Environment Check: Pandas={HAS_PANDAS}, Matplotlib={HAS_MATPLOTLIB}")


# ------------------------------------------------------------------------------
# 1. DATA RETRIEVAL (From SQLite Database or CSV Fallback)
# ------------------------------------------------------------------------------
def retrieve_data():
    """
    Requirement 6.1: Retrieve electricity consumption data from the database.
    """
    records = []
    
    # Try SQLite first
    if os.path.exists(DB_PATH):
        try:
            conn = sqlite3.connect(DB_PATH)
            query = """
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
            cursor = conn.cursor()
            cursor.execute(query)
            headers = [desc[0] for desc in cursor.description]
            for row in cursor.fetchall():
                records.append(dict(zip(headers, row)))
            conn.close()
            print(f" [+] Retrieved {len(records)} records directly from SQLite database: {DB_PATH}")
            return records
        except Exception as e:
            print(f" [!] SQLite query error: {e}. Falling back to CSV...")

    # Fallback to CSV
    if os.path.exists(CSV_PATH):
        with open(CSV_PATH, 'r', encoding='utf-8') as f:
            reader = csv.DictReader(f)
            for row in reader:
                # Type conversions
                row['reading_id'] = int(row['reading_id'])
                row['previous_reading'] = float(row['previous_reading'])
                row['current_reading'] = float(row['current_reading'])
                row['units_consumed'] = float(row['units_consumed'])
                row['energy_charges'] = float(row.get('energy_charges', 0.0))
                row['fixed_charges'] = float(row.get('fixed_charges', 0.0))
                row['tax_amount'] = float(row.get('tax_amount', 0.0))
                row['bill_total'] = float(row.get('bill_total', 0.0))
                records.append(row)
        print(f" [+] Loaded {len(records)} records from CSV: {CSV_PATH}")
        return records

    # In-memory default data if database has not yet been initialized
    print(" [!] No database or CSV file found. Generating realistic sample dataset...")
    return generate_mock_data()


def generate_mock_data():
    """Generates synthetic dataset if files are missing."""
    consumers = [
        ('C101', 'Aarav Sharma', 'Domestic', 'MTR-DOM-101', [140.0, 165.0, 220.0, 270.0]),
        ('C102', 'Priya Patel', 'Domestic', 'MTR-DOM-102', [80.0, 95.0, 120.0, 145.0]),
        ('C103', 'Rajesh Verma', 'Domestic', 'MTR-DOM-103', [280.0, 340.0, 540.0, 650.0]),
        ('C104', 'Ananya Gupta', 'Domestic', 'MTR-DOM-104', [110.0, 130.0, 170.0, 220.0]),
        ('C201', 'Apex Tech Solutions', 'Commercial', 'MTR-COM-201', [850.0, 930.0, 1100.0, 1260.0]),
        ('C202', 'Metro Central Mart', 'Commercial', 'MTR-COM-202', [650.0, 690.0, 780.0, 890.0]),
        ('C301', 'Falcon Heavy Steels', 'Industrial', 'MTR-IND-301', [4200.0, 4600.0, 5300.0, 5850.0]),
        ('C302', 'Sunrise Textile Mills', 'Industrial', 'MTR-IND-302', [2800.0, 2950.0, 3200.0, 3450.0]),
        ('C401', 'Kisan Agro Coop', 'Agricultural', 'MTR-AGR-401', [350.0, 430.0, 540.0, 660.0]),
        ('C402', 'Greenfield Dairy', 'Agricultural', 'MTR-AGR-402', [280.0, 320.0, 380.0, 450.0]),
    ]
    months = ['2024-01', '2024-02', '2024-03', '2024-04']
    records = []
    rid = 1
    for cid, name, ctype, meter, usage_list in consumers:
        base = 1000.0
        for i, m in enumerate(months):
            u = usage_list[i]
            prev = base
            curr = base + u
            base = curr
            tot = u * (4.5 if ctype=='Domestic' else (9.5 if ctype=='Commercial' else (11.0 if ctype=='Industrial' else 2.0)))
            records.append({
                'consumer_id': cid, 'name': name, 'connection_type': ctype,
                'address': 'Main Road', 'meter_number': meter, 'reading_id': rid,
                'billing_month': m, 'reading_date': f"{m}-28", 'previous_reading': prev,
                'current_reading': curr, 'units_consumed': u, 'energy_charges': tot * 0.85,
                'fixed_charges': 100.0, 'tax_amount': tot * 0.05, 'bill_total': tot,
                'payment_status': 'Paid' if i < 3 else 'Unpaid'
            })
            rid += 1
    return records


# ------------------------------------------------------------------------------
# 2. DATA PREPROCESSING & STATISTICAL AUDIT
# ------------------------------------------------------------------------------
def preprocess_and_analyze(records):
    """
    Requirement 6.2: Preprocess data and perform energy analytics.
    """
    print("\n" + "="*75)
    print("      ELECTRICITY CONSUMPTION & ENERGY USAGE STATISTICAL AUDIT REPORT")
    print("="*75)
    
    total_records = len(records)
    total_units = sum(r['units_consumed'] for r in records)
    total_revenue = sum(r['bill_total'] for r in records)
    avg_units = total_units / total_records if total_records > 0 else 0
    
    print(f" Total Consumption Records : {total_records}")
    print(f" Total Electricity Consumed: {total_units:,.2f} kWh")
    print(f" Gross Billing Revenue     : Rs. {total_revenue:,.2f}")
    print(f" Mean Units per Record     : {avg_units:.2f} kWh")
    print("-" * 75)

    # Group by Connection Type
    by_category = {}
    for r in records:
        cat = r['connection_type']
        if cat not in by_category:
            by_category[cat] = {'units': [], 'revenue': 0.0}
        by_category[cat]['units'].append(r['units_consumed'])
        by_category[cat]['revenue'] += r['bill_total']

    print(" USAGE & REVENUE DISTRIBUTION BY CONNECTION TYPE:")
    print(f" {'Category':<14} | {'Count':<6} | {'Total Units (kWh)':<18} | {'Share %':<8} | {'Avg Units':<10} | {'Revenue (Rs)':<14}")
    print(" " + "-"*73)
    for cat, data in sorted(by_category.items(), key=lambda x: sum(x[1]['units']), reverse=True):
        cat_units = sum(data['units'])
        cat_count = len(data['units'])
        cat_mean = cat_units / cat_count
        share = (cat_units / total_units) * 100.0
        print(f" {cat:<14} | {cat_count:<6} | {cat_units:18,.2f} | {share:7.2f}% | {cat_mean:10.2f} | Rs. {data['revenue']:12,.2f}")
    print("-" * 75)

    # Monthly Trends
    by_month = {}
    for r in records:
        m = r['billing_month']
        by_month[m] = by_month.get(m, 0.0) + r['units_consumed']

    print(" MONTHLY CONSUMPTION PROGRESSION:")
    prev_month_units = None
    for m, u in sorted(by_month.items()):
        growth_str = ""
        if prev_month_units:
            growth = ((u - prev_month_units) / prev_month_units) * 100.0
            growth_str = f"({'+' if growth >= 0 else ''}{growth:.2f}% MoM)"
        print(f"  * Month {m}: {u:12,.2f} kWh  {growth_str}")
        prev_month_units = u
    print("-" * 75)

    # High Consumption Analysis (Threshold >= 500 units)
    high_consumers = [r for r in records if r['units_consumed'] >= 500]
    high_consumers.sort(key=lambda x: x['units_consumed'], reverse=True)

    print(f" HIGH CONSUMPTION CONSUMERS DETECTED (Threshold >= 500 kWh):")
    print(f" Total flagged instances: {len(high_consumers)}")
    print(f" {'ID':<6} | {'Name':<22} | {'Category':<12} | {'Month':<8} | {'Units (kWh)':<12} | {'Severity'}")
    print(" " + "-"*73)
    for h in high_consumers[:10]: # Top 10
        u = h['units_consumed']
        sev = "CRITICAL" if u >= 3000 else ("SEVERE" if u >= 1000 else "HIGH")
        print(f" {h['consumer_id']:<6} | {h['name'][:22]:<22} | {h['connection_type']:<12} | {h['billing_month']:<8} | {u:12.2f} | [{sev}]")
    print("=" * 75 + "\n")

    return {
        'total_units': total_units,
        'total_revenue': total_revenue,
        'by_category': by_category,
        'by_month': by_month,
        'high_consumers': high_consumers
    }


# ------------------------------------------------------------------------------
# 3. VISUALIZATION ENGINE (Matplotlib or Native SVG/HTML Dashboard)
# ------------------------------------------------------------------------------
def generate_visualizations(records, stats):
    """
    Requirement 6.3: Generate visualizations using Python.
    Generates high-resolution PNG charts via Matplotlib if installed.
    Also generates a complete self-contained SVG & HTML Dashboard.
    """
    print("[*] Generating Data Visualizations...")

    if HAS_MATPLOTLIB and HAS_PANDAS:
        generate_matplotlib_visuals(records)
    else:
        print(" [!] Matplotlib or Pandas not detected in current Python environment.")
        print(" [!] Generating standalone SVG charts and Interactive HTML Dashboard...")

    # Always generate the interactive dashboard as an additional asset!
    generate_html_svg_dashboard(records, stats)


def generate_matplotlib_visuals(records):
    """Creates publication-ready PNG figures using Matplotlib and Pandas."""
    df = pd.DataFrame(records)

    # 1. Monthly Consumption Trend by Connection Type
    plt.figure(figsize=(10, 6))
    pivot_monthly = df.pivot_table(index='billing_month', columns='connection_type', values='units_consumed', aggfunc='sum').fillna(0)
    for col in pivot_monthly.columns:
        plt.plot(pivot_monthly.index, pivot_monthly[col], marker='o', linewidth=2.5, label=col)
    
    plt.title('Monthly Electricity Consumption Trend by Sector', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Billing Month', fontsize=12)
    plt.ylabel('Electricity Consumed (kWh)', fontsize=12)
    plt.grid(True, linestyle='--', alpha=0.6)
    plt.legend(title='Connection Type', frameon=True)
    plt.tight_layout()
    trend_path = os.path.join(OUTPUT_DIR, '1_monthly_consumption_trend.png')
    plt.savefig(trend_path, dpi=300)
    plt.close()
    print(f" [+] Saved: {trend_path}")

    # 2. Consumption Share Donut Chart & Boxplot
    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(14, 6))
    cat_totals = df.groupby('connection_type')['units_consumed'].sum()
    colors = ['#4285F4', '#EA4335', '#FBBC05', '#34A853']
    ax1.pie(cat_totals, labels=cat_totals.index, autopct='%1.1f%%', startangle=140, 
            colors=colors, wedgeprops=dict(width=0.4, edgecolor='w', linewidth=2))
    ax1.set_title('Energy Consumption Share by Sector', fontsize=13, fontweight='bold')

    df.boxplot(column='units_consumed', by='connection_type', ax=ax2, grid=True, patch_artist=True)
    ax2.set_title('Consumption Distribution (Spread & Outliers)', fontsize=13, fontweight='bold')
    ax2.set_xlabel('Connection Type')
    ax2.set_ylabel('Units (kWh)')
    plt.suptitle('') # Clear automatic boxplot title
    plt.tight_layout()
    share_path = os.path.join(OUTPUT_DIR, '2_consumption_by_connection_type.png')
    plt.savefig(share_path, dpi=300)
    plt.close()
    print(f" [+] Saved: {share_path}")

    # 3. High Consumption Outliers Bar Chart
    plt.figure(figsize=(10, 6))
    top_consumers = df.groupby(['consumer_id', 'name', 'connection_type'])['units_consumed'].mean().reset_index()
    top_consumers = top_consumers.sort_values(by='units_consumed', ascending=True).tail(8)
    
    bar_colors = ['#d9534f' if u >= 1000 else ('#f0ad4e' if u >= 500 else '#5cb85c') for u in top_consumers['units_consumed']]
    bars = plt.barh(top_consumers['name'] + " (" + top_consumers['connection_type'] + ")", top_consumers['units_consumed'], color=bar_colors)
    plt.axvline(x=500, color='orange', linestyle='--', linewidth=1.5, label='High Threshold (500 kWh)')
    plt.axvline(x=1000, color='red', linestyle='--', linewidth=1.5, label='Severe Threshold (1000 kWh)')
    plt.title('Top Energy Consumers (Average Monthly kWh)', fontsize=14, fontweight='bold', pad=15)
    plt.xlabel('Average Units Consumed (kWh)', fontsize=12)
    plt.legend()
    plt.grid(axis='x', linestyle='--', alpha=0.6)
    plt.tight_layout()
    high_path = os.path.join(OUTPUT_DIR, '3_high_consumption_consumers.png')
    plt.savefig(high_path, dpi=300)
    plt.close()
    print(f" [+] Saved: {high_path}")


def generate_html_svg_dashboard(records, stats):
    """
    Creates a rich, self-contained SVG & HTML Dashboard that opens in any browser
    without requiring external dependencies.
    """
    dashboard_file = os.path.join(OUTPUT_DIR, 'energy_usage_dashboard.html')

    # Calculate monthly data for SVG line chart
    months = sorted(list(stats['by_month'].keys()))
    month_vals = [stats['by_month'][m] for m in months]
    max_m_val = max(month_vals) if month_vals else 1

    # Generate points for SVG line chart (width: 500, height: 200, padding: 40)
    chart_w = 460
    chart_h = 160
    points = []
    labels_svg = []
    for idx, m in enumerate(months):
        x = 40 + (idx * (chart_w / max(1, len(months)-1)))
        y = 180 - ((month_vals[idx] / max_m_val) * chart_h)
        points.append(f"{x:.1f},{y:.1f}")
        labels_svg.append(f'<text x="{x:.1f}" y="195" font-size="11" text-anchor="middle" fill="#666">{m}</text>')
        labels_svg.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="4" fill="#1a73e8" />')
        labels_svg.append(f'<text x="{x:.1f}" y="{y-8:.1f}" font-size="10" font-weight="bold" text-anchor="middle" fill="#1a73e8">{int(month_vals[idx])}</text>')

    polyline_str = " ".join(points)

    # High consumers table rows
    high_rows = ""
    for h in stats['high_consumers'][:8]:
        u = h['units_consumed']
        badge_class = "danger" if u >= 3000 else ("warning" if u >= 1000 else "info")
        sev_label = "CRITICAL" if u >= 3000 else ("SEVERE" if u >= 1000 else "HIGH")
        high_rows += f"""
        <tr>
            <td><strong>{h['consumer_id']}</strong></td>
            <td>{h['name']}</td>
            <td><span class="badge {h['connection_type'].lower()}">{h['connection_type']}</span></td>
            <td>{h['billing_month']}</td>
            <td><strong>{u:,.2f} kWh</strong></td>
            <td><span class="badge {badge_class}">{sev_label}</span></td>
        </tr>
        """

    # Category breakdown rows
    cat_rows = ""
    for cat, data in sorted(stats['by_category'].items(), key=lambda x: sum(x[1]['units']), reverse=True):
        u = sum(data['units'])
        pct = (u / stats['total_units']) * 100.0
        cat_rows += f"""
        <tr>
            <td><strong>{cat}</strong></td>
            <td>{len(data['units'])} meters</td>
            <td>{u:,.2f} kWh</td>
            <td>
                <div class="progress-bar-bg">
                    <div class="progress-bar-fill" style="width: {pct:.1f}%;"></div>
                </div>
                <span>{pct:.1f}%</span>
            </td>
            <td>Rs. {data['revenue']:,.2f}</td>
        </tr>
        """

    html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Electricity Consumption Management & Energy Usage Analysis</title>
    <style>
        body {{
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            background: #f4f6f9;
            color: #333;
            margin: 0;
            padding: 24px;
        }}
        .header {{
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
            color: #fff;
            padding: 24px 32px;
            border-radius: 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            margin-bottom: 24px;
        }}
        .header h1 {{ margin: 0 0 6px 0; font-size: 26px; }}
        .header p {{ margin: 0; opacity: 0.9; font-size: 14px; }}
        .kpi-grid {{
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
            gap: 16px;
            margin-bottom: 24px;
        }}
        .kpi-card {{
            background: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.05);
            border-left: 4px solid #1a73e8;
        }}
        .kpi-title {{ font-size: 13px; color: #666; text-transform: uppercase; margin-bottom: 6px; font-weight: 600; }}
        .kpi-val {{ font-size: 24px; font-weight: bold; color: #111; }}
        .grid-2 {{
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 24px;
        }}
        @media (max-width: 900px) {{ .grid-2 {{ grid-template-columns: 1fr; }} }}
        .card {{
            background: #fff;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.05);
        }}
        .card h3 {{ margin-top: 0; color: #1e3c72; border-bottom: 2px solid #f0f2f5; padding-bottom: 10px; font-size: 17px; }}
        table {{ width: 100%; border-collapse: collapse; font-size: 13px; }}
        th, td {{ padding: 10px 12px; text-align: left; border-bottom: 1px solid #edf0f2; }}
        th {{ background: #f8f9fa; color: #555; }}
        .badge {{
            display: inline-block;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: bold;
        }}
        .badge.domestic {{ background: #e8f0fe; color: #1967d2; }}
        .badge.commercial {{ background: #fce8e6; color: #c5221f; }}
        .badge.industrial {{ background: #fef7e0; color: #b06000; }}
        .badge.agricultural {{ background: #e6f4ea; color: #137333; }}
        .badge.danger {{ background: #fce8e6; color: #c5221f; }}
        .badge.warning {{ background: #fef7e0; color: #b06000; }}
        .badge.info {{ background: #e8f0fe; color: #1a73e8; }}
        .progress-bar-bg {{ background: #e9ecef; border-radius: 4px; height: 8px; display: inline-block; width: 60px; vertical-align: middle; margin-right: 6px; }}
        .progress-bar-fill {{ background: #1a73e8; height: 100%; border-radius: 4px; }}
    </style>
</head>
<body>
    <div class="header">
        <h1>⚡ Electricity Consumption & Energy Usage Analytics</h1>
        <p>Scenario 14: Automated Energy Billing, High-Consumption Detection & Visual Insights (Java &bull; DBMS &bull; Python)</p>
    </div>

    <div class="kpi-grid">
        <div class="kpi-card" style="border-left-color: #1a73e8;">
            <div class="kpi-title">Total Electricity Consumed</div>
            <div class="kpi-val">{stats['total_units']:,.2f} <small style="font-size:14px;">kWh</small></div>
        </div>
        <div class="kpi-card" style="border-left-color: #34a853;">
            <div class="kpi-title">Total Billed Revenue</div>
            <div class="kpi-val">Rs. {stats['total_revenue']:,.2f}</div>
        </div>
        <div class="kpi-card" style="border-left-color: #ea4335;">
            <div class="kpi-title">High-Consumption Flags</div>
            <div class="kpi-val">{len(stats['high_consumers'])} <small style="font-size:14px;">Alerts</small></div>
        </div>
        <div class="kpi-card" style="border-left-color: #fbbc05;">
            <div class="kpi-title">Total Active Meters</div>
            <div class="kpi-val">{len(records)} <small style="font-size:14px;">Readings</small></div>
        </div>
    </div>

    <div class="grid-2">
        <div class="card">
            <h3>📈 Monthly Electricity Consumption Trend</h3>
            <svg viewBox="0 0 540 210" style="width: 100%; height: auto;">
                <!-- Grid Lines -->
                <line x1="40" y1="20" x2="500" y2="20" stroke="#eee" />
                <line x1="40" y1="100" x2="500" y2="100" stroke="#eee" />
                <line x1="40" y1="180" x2="500" y2="180" stroke="#ccc" />
                <!-- Trend line -->
                <polyline fill="none" stroke="#1a73e8" stroke-width="3" stroke-linecap="round" points="{polyline_str}" />
                <!-- Labels & Points -->
                {' '.join(labels_svg)}
            </svg>
            <p style="font-size: 12px; color: #777; text-align: center; margin-top: 8px;">
                Consumption peaks in Summer (March-April) driven by cooling demand and industrial throughput.
            </p>
        </div>

        <div class="card">
            <h3>⚡ Category Breakdown & Revenue Contribution</h3>
            <table>
                <thead>
                    <tr>
                        <th>Category</th>
                        <th>Meters</th>
                        <th>Consumption</th>
                        <th>Share</th>
                        <th>Revenue</th>
                    </tr>
                </thead>
                <tbody>
                    {cat_rows}
                </tbody>
            </table>
        </div>
    </div>

    <div class="card">
        <h3>🚨 High-Consumption Consumers Alert (Threshold &ge; 500 kWh)</h3>
        <table>
            <thead>
                <tr>
                    <th>Consumer ID</th>
                    <th>Consumer Name</th>
                    <th>Connection Type</th>
                    <th>Billing Month</th>
                    <th>Units Consumed</th>
                    <th>Severity Alert</th>
                </tr>
            </thead>
            <tbody>
                {high_rows}
            </tbody>
        </table>
    </div>
</body>
</html>
"""
    with open(dashboard_file, 'w', encoding='utf-8') as f:
        f.write(html_content)
    print(f" [+] Generated Interactive Visual Dashboard: {dashboard_file}")


# ------------------------------------------------------------------------------
# MAIN EXECUTION PIPELINE
# ------------------------------------------------------------------------------
def main():
    print("="*75)
    print(" ⚡ ENERGY USAGE ANALYSIS SYSTEM - PYTHON DATA ENGINE")
    print("="*75)
    
    # Step 1: Retrieve Data
    records = retrieve_data()
    if not records:
        print("[!] No records found to analyze.")
        return

    # Step 2: Preprocess & Statistical Analytics
    stats = preprocess_and_analyze(records)

    # Step 3: Visualizations
    generate_visualizations(records, stats)

    print("\n[✓] Energy Usage Analysis and Visualization execution completed successfully!")
    print(f"[*] Visual assets saved in: {OUTPUT_DIR}")


if __name__ == '__main__':
    main()
