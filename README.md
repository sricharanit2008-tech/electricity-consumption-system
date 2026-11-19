# Electricity Consumption Management and Energy Usage Analysis System
**Scenario 14 | Register Numbers: 732925ITR104**  
*Technology Stack: Java (Core/JDBC), DBMS (MySQL/SQLite), Python (Pandas/Matplotlib/Seaborn)*

---

## 1. Problem Statement
An electricity distribution office maintains consumer details, meter readings, monthly electricity consumption, and billing records. Managing these records manually can make it difficult to calculate electricity bills accurately, identify high-consumption consumers, and analyze changes in energy usage over time.

This project delivers an integrated **Electricity Consumption Management and Energy Usage Analysis System** designed to:
- Maintain consumer master records and meter specifications.
- Record monthly previous and current meter readings with auto-consumption computation.
- Compute electricity bills via tiered tariff slabs, fixed charges, and taxes.
- Generate consumer invoices, monthly audit reports, and high-consumption alert lists.
- Extract consumption metrics from the database, preprocess them, and generate visual analytics using Python.

---

## 2. Functional Requirements Coverage

| Req # | Functional Requirement | Implementation Component |
| :---: | :--- | :--- |
| **1** | Maintain consumer details (ID, name, address, connection type) | `consumers` table, `Consumer.java`, `ConsumerDAO.java`, Main Menu Option 1 & 2 |
| **2** | Record previous and current meter readings for each billing period | `meter_readings` table, `MeterReading.java`, `MeterReadingDAO.java`, Menu Option 3 |
| **3** | Calculate electricity consumption (`units = current - previous`) | `MeterReading.calculateUnitsConsumed()`, SQL validation check, Menu Option 3 |
| **4** | Calculate electricity bills using predefined tariff rates & charges | `tariff_slabs` table, `BillingService.java`, progressive tiered slabs, Menu Option 4 |
| **5.1** | Generate consumer-wise bills (detailed invoice) | `Bill.java`, `ReportService.printConsumerBill()`, Menu Option 5 |
| **5.2** | Generate monthly consumption reports | `ReportService.printMonthlyConsumptionReport()`, SQL query, Menu Option 6 |
| **5.3** | Lists of high-consumption consumers (with severity alerts) | `ReportService.printHighConsumptionReport()`, SQL query, Menu Option 7 |
| **6** | Retrieve DB data, preprocess & generate Python visualizations | `energy_analysis.py`, SQLite/CSV pipeline, Pandas, Matplotlib, SVG dashboard |

---

## 3. System Architecture & ER Diagram

```mermaid
erDiagram
    CONSUMERS ||--o{ METER_READINGS : has
    CONSUMERS ||--o{ BILLS : billed_to
    METER_READINGS ||--|| BILLS : generates
    TARIFF_SLABS }o--|| CONSUMERS : applies_to_category

    CONSUMERS {
        string consumer_id PK
        string name
        string address
        string phone
        string connection_type
        string meter_number UK
        date connection_date
        string status
    }

    TARIFF_SLABS {
        int slab_id PK
        string connection_type
        int min_units
        int max_units
        decimal rate_per_unit
        decimal fixed_charge
        decimal tax_percent
    }

    METER_READINGS {
        int reading_id PK
        string consumer_id FK
        string billing_month
        date reading_date
        decimal previous_reading
        decimal current_reading
        decimal units_consumed
    }

    BILLS {
        string bill_id PK
        int reading_id FK
        string consumer_id FK
        string billing_month
        decimal units_consumed
        decimal energy_charges
        decimal fixed_charges
        decimal tax_amount
        decimal total_amount
        date bill_date
        date due_date
        string payment_status
    }
```

---

## 4. Predefined Tariff Slab Structure

The billing engine applies progressive slabs based on the consumer's connection type:

### 🏠 Domestic
- **0 – 100 units**: Rs. 3.50 / unit | Fixed Charge: Rs. 50.00 | Tax: 5%
- **101 – 300 units**: Rs. 5.20 / unit | Fixed Charge: Rs. 75.00 | Tax: 5%
- **301 – 500 units**: Rs. 7.50 / unit | Fixed Charge: Rs. 100.00 | Tax: 5%
- **Above 500 units**: Rs. 9.80 / unit | Fixed Charge: Rs. 150.00 | Tax: 5%

### 🏢 Commercial
- **0 – 200 units**: Rs. 8.50 / unit | Fixed Charge: Rs. 200.00 | Tax: 9%
- **201 – 750 units**: Rs. 11.00 / unit | Fixed Charge: Rs. 350.00 | Tax: 9%
- **Above 750 units**: Rs. 13.50 / unit | Fixed Charge: Rs. 500.00 | Tax: 9%

### 🏭 Industrial
- **0 – 1000 units**: Rs. 9.00 / unit | Fixed Charge: Rs. 800.00 | Tax: 12%
- **Above 1000 units**: Rs. 12.50 / unit | Fixed Charge: Rs. 1500.00 | Tax: 12%

### 🌾 Agricultural
- **0 – 500 units**: Rs. 1.50 / unit | Fixed Charge: Rs. 30.00 | Tax: 2%
- **Above 500 units**: Rs. 2.80 / unit | Fixed Charge: Rs. 50.00 | Tax: 2%

---

## 5. Directory Structure

```
electricity-consumption-system/
│
├── database/
│   ├── schema.sql              # Relational schema (DDL) for SQLite & MySQL
│   ├── sample_data.sql         # 48+ realistic historical records & consumers (DML)
│   ├── queries.sql             # Analytical queries (high consumers, aggregates)
│   ├── electricity.db          # Pre-built SQLite database file
│   ├── energy_data.csv         # Exported CSV analytical dataset
│   └── init_database.py        # Automated DB generator script
│
├── java_app/
│   ├── compile_and_run.bat     # Windows batch launcher
│   ├── run.sh                  # Linux / macOS bash launcher
│   └── src/com/electricity/
│       ├── Main.java           # Interactive CLI management console
│       ├── model/
│       │   ├── Consumer.java
│       │   ├── MeterReading.java
│       │   ├── TariffSlab.java
│       │   └── Bill.java
│       ├── dao/
│       │   ├── DBConnection.java
│       │   ├── ConsumerDAO.java
│       │   ├── MeterReadingDAO.java
│       │   └── BillDAO.java
│       └── service/
│           ├── BillingService.java
│           └── ReportService.java
│
└── python_analysis/
    ├── energy_analysis.py      # Data retrieval, preprocessing & visualizer
    ├── requirements.txt        # pandas, numpy, matplotlib, seaborn
    └── output/
        ├── 1_monthly_consumption_trend.png
        ├── 2_consumption_by_connection_type.png
        ├── 3_high_consumption_consumers.png
        └── energy_usage_dashboard.html # Interactive visual dashboard
```

---

## 6. How to Run

### Step 1: Initialize Database
```bash
cd database
python init_database.py
```
*Creates `electricity.db` and exports `energy_data.csv`.*

### Step 2: Compile & Run the Java Application
**On Windows:**
Double-click `java_app\compile_and_run.bat` or run:
```cmd
cd java_app
javac -d bin src/com/electricity/model/*.java src/com/electricity/dao/*.java src/com/electricity/service/*.java src/com/electricity/Main.java
java -cp bin com.electricity.Main
```

### Step 3: Run Python Visualizations & Energy Analytics
```cmd
cd python_analysis
pip install -r requirements.txt
python energy_analysis.py
```
*Outputs charts to `python_analysis/output/` and displays the audit summary in your terminal.*

---

## 7. Viva & Lab Examination Q&A

**Q1: How is progressive slab billing calculated?**  
*A:* Rather than charging all units at a single maximum rate, units are allocated into tiers. For example, for 250 units under Domestic:
- First 100 units @ Rs. 3.50 = Rs. 350
- Remaining 150 units @ Rs. 5.20 = Rs. 780
- Total Energy Charges = Rs. 1,130 + Fixed Charge (Rs. 75) + 5% Tax.

**Q2: How does the system satisfy Requirement 3 (`units = current - previous`)?**  
*A:* In `MeterReading.java`, `calculateUnitsConsumed()` computes `currentReading - previousReading` and validates `currentReading >= previousReading`. The database also enforces `CONSTRAINT chk_readings CHECK (current_reading >= previousReading)`.

**Q3: How are high-consumption consumers identified in Python?**  
*A:* Through multi-level criteria:
1. Static threshold filter (`units_consumed >= 500 kWh`).
2. Severity categorization (`CRITICAL` >= 3000, `SEVERE` >= 1000, `HIGH` >= 500).
3. Outlier detection comparing individual usage against the category mean.
