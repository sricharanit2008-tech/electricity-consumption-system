# Part 3: Python Analytics & Visualization Presentation Guide
**Scenario 14: Electricity Consumption Management System**  
**Student Register Number: 732925ITR104**

---

## 1. Objective of the Python Module
To fulfill **Functional Requirement 6**:
> *"Retrieve electricity consumption data from the database, preprocess it, and generate visualizations using Python."*

Key functions performed:
1. Connects to `electricity.db` via `sqlite3` and executes analytical SQL joins.
2. Preprocesses data using Pandas/NumPy (handles missing values, computes Month-over-Month growth rates, categorizes severity).
3. Generates high-resolution visualizations using Matplotlib/Seaborn and interactive SVG dashboards.
4. Outputs an executive energy audit report.

---

## 2. Analytics Pipeline Architecture

```
[database/electricity.db] (SQLite)
           |
           v
[retrieve_data()] ------------> Executes joined SQL query (Readings + Consumers + Bills)
           |
           v
[preprocess_and_analyze()] ---> Pandas Dataframe:
                                - Computes MoM Growth %
                                - Groups by Connection Type & Billing Month
                                - Flags High-Consumption Outliers (>= 500 kWh)
           |
           v
[generate_visualizations()] --> Generates:
                                1. Monthly Consumption Trend Line Plot
                                2. Sector Share Donut Chart & Boxplot
                                3. Top Consumers Outlier Bar Chart
                                4. Interactive HTML & SVG Dashboard (energy_usage_dashboard.html)
```

---

## 3. Key Code Highlights to Show the Professor

Located in [`python_analysis/energy_analysis.py`](../python_analysis/energy_analysis.py):

### 1. Database Data Retrieval via SQLite Join
```python
def retrieve_data():
    conn = sqlite3.connect(DB_PATH)
    query = """
    SELECT 
        c.consumer_id, c.name, c.connection_type, c.meter_number,
        mr.reading_id, mr.billing_month, mr.previous_reading, mr.current_reading,
        mr.units_consumed, COALESCE(b.total_amount, 0.0) AS bill_total
    FROM meter_readings mr
    JOIN consumers c ON mr.consumer_id = c.consumer_id
    LEFT JOIN bills b ON mr.reading_id = b.reading_id
    ORDER BY mr.billing_month, c.connection_type;
    """
    cursor = conn.cursor()
    cursor.execute(query)
    # Returns clean records list / DataFrame
```

### 2. Preprocessing & Month-over-Month (MoM) Growth Analysis
```python
by_month = {}
for r in records:
    m = r['billing_month']
    by_month[m] = by_month.get(m, 0.0) + r['units_consumed']

# MoM Percentage Growth Formula:
# Growth % = ((Current_Month_Units - Prev_Month_Units) / Prev_Month_Units) * 100
prev_month_units = None
for m, u in sorted(by_month.items()):
    if prev_month_units:
        growth = ((u - prev_month_units) / prev_month_units) * 100.0
        print(f"Month {m}: {u:,.2f} kWh ({growth:+.2f}% MoM)")
    prev_month_units = u
```

### 3. Outlier Detection (High Consumption Thresholds)
```python
high_consumers = [r for r in records if r['units_consumed'] >= 500]
high_consumers.sort(key=lambda x: x['units_consumed'], reverse=True)

# Classifies severity
for h in high_consumers:
    u = h['units_consumed']
    severity = "CRITICAL" if u >= 3000 else ("SEVERE" if u >= 1000 else "HIGH")
```

---

## 4. Visualizations Generated

| Chart File | Visualization Type | Key Insight Provided |
| :--- | :--- | :--- |
| `1_monthly_consumption_trend.png` | Multi-line Trend Plot with Markers | Demonstrates summer energy surge (+16.3% in March, +12.6% in April). |
| `2_consumption_by_connection_type.png` | Donut Chart & Boxplot | Shows Industrial sector consumes **65.8%** of total power, with high variance. |
| `3_high_consumption_consumers.png` | Horizontal Bar Chart with Thresholds | Highlights critical consumers (e.g. `C301 Falcon Heavy Steels` at 5,850 kWh). |
| `energy_usage_dashboard.html` | Self-contained Interactive Dashboard | Complete KPI cards, SVG trendlines, and live web portal. |

---

## 5. How to Run the Demo for the Professor

```cmd
cd python_analysis
python energy_analysis.py
```
Output displays the statistical audit directly in the terminal and generates all visual chart assets in `python_analysis/output/`.

---

## 6. Typical Python & Data Analytics Viva Questions & Model Answers

* **Q: Why did you use Pandas for preprocessing instead of raw Python lists?**  
  *A:* Pandas provides optimized vectorized operations, rapid grouping (`groupby`), pivoting, and memory-efficient data structures for handling thousands of telemetry records.

* **Q: How did you define and detect high-consumption consumers?**  
  *A:* We used a two-tier approach:
  1. **Threshold Filtering:** Flagging readings exceeding $500\text{ kWh}$.
  2. **Categorical Severity Classification:** `[CRITICAL]` ($\ge 3000\text{ kWh}$), `[SEVERE]` ($\ge 1000\text{ kWh}$), and `[HIGH]` ($\ge 500\text{ kWh}$).

* **Q: What actionable insights did the data visualization reveal?**  
  *A:* 
  1. Industrial consumers represent only ~17% of accounts but account for **65.8%** of total power consumption.
  2. A pronounced seasonal spike occurred in March (+16.32% MoM) and April (+12.62% MoM) due to cooling demand.
