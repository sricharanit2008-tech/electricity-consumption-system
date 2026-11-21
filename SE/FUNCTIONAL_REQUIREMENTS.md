# Functional Requirements Specification (FRS)
**Project:** Electricity Consumption Management and Energy Usage Analysis System  
**Scenario:** Scenario 14  
**Student Register Number:** 732925ITR104  
**Standard:** IEEE 830 Recommended Practice for Software Requirements Specifications  

---

## 1. System Overview & Scope
The Electricity Consumption Management and Energy Usage Analysis System is designed to automate utility distribution management, replace error-prone manual ledger operations, eliminate billing discrepancies, compute multi-tier progressive electricity bills, detect high-consumption energy outliers, and deliver graphical telemetry visualization.

---

## 2. Detailed Functional Requirements (FR)

### FR-1: Consumer Master Record & Category Management
* **Description:** The system shall maintain complete consumer records with demographic, contact, meter, and connection category specifications.
* **Inputs:** Consumer ID, Full Name, Premise Address, Phone Number, Connection Type (`Domestic`, `Commercial`, `Industrial`, `Agricultural`), Meter Serial Number.
* **Processing:**
  1. Validates that Consumer ID is unique and follows the utility alphanumeric schema (e.g., `C101`).
  2. Ensures Connection Type belongs to an authorized category.
  3. Automatically links a unique meter serial number and records initial connection date (`LocalDate.now()`).
  4. Provides CRUD operations: Add new consumer, View all consumers, Retrieve by ID, and Update existing premise/contact/status details.
* **Outputs:** Success confirmation, persisted consumer entity in database table `consumers`, formatted consumer table view.
* **Implementation:** `consumers` table, `Consumer.java`, `ConsumerDAO.java`, Main Menu Options 1, 2, 3.

---

### FR-2: Meter Reading Telemetry Recording
* **Description:** The system shall capture previous and current meter readings for each consumer across successive monthly billing periods.
* **Inputs:** Consumer ID, Billing Period (`YYYY-MM`), Reading Date, Previous Meter Reading (kWh), Current Meter Reading (kWh).
* **Processing:**
  1. Verifies that the specified Consumer ID exists in the active registry.
  2. Automatically suggests or fetches the consumer's latest recorded reading as the default previous reading.
  3. Validates that `Current Reading >= Previous Reading` to avoid anomalous or rollover entries.
  4. Stores the record with primary key `reading_id` and foreign key reference to `consumer_id`.
* **Outputs:** Stored reading record, validation confirmation.
* **Implementation:** `meter_readings` table, `MeterReading.java`, `MeterReadingDAO.java`, Main Menu Option 4.

---

### FR-3: Automated Electricity Consumption Calculation
* **Description:** The system shall compute the net energy consumed during the billing cycle by subtracting previous reading from current reading.
* **Mathematical Formula:**
  $$\text{Units Consumed (kWh)} = \text{Current Meter Reading} - \text{Previous Meter Reading}$$
* **Business Rules & Constraints:**
  * **BR-3.1:** $\text{Units Consumed} \ge 0.00$. If $\text{Current Reading} < \text{Previous Reading}$, the system must reject input and raise an `IllegalArgumentException`.
  * **BR-3.2:** Database-level enforcement via constraint: `CONSTRAINT chk_readings CHECK (current_reading >= previous_reading)`.
  * **BR-3.3:** Precision rounded to 2 decimal places.
* **Outputs:** Calculated net energy units in kWh stored in `meter_readings.units_consumed`.
* **Implementation:** `MeterReading.calculateUnitsConsumed()`, SQL Check Constraint, Main Menu Option 4.

---

### FR-4: Tiered Tariff Slab & Multi-Charge Billing Engine
* **Description:** The system shall calculate monthly electricity bills using predefined progressive tiered tariff rates, fixed monthly demand charges, and statutory tax/cess percentages based on connection category.
* **Predefined Tariff Structure:**
  1. **Domestic Connection:**
     * 0 – 100 kWh: Rs. 3.50 / kWh
     * 101 – 300 kWh: Rs. 5.20 / kWh
     * 301 – 500 kWh: Rs. 7.50 / kWh
     * Above 500 kWh: Rs. 9.80 / kWh
     * Fixed Demand Charge: Rs. 50.00 – Rs. 150.00 | Tax (GST): 5.0%
  2. **Commercial Connection:**
     * 0 – 200 kWh: Rs. 8.50 / kWh
     * 201 – 750 kWh: Rs. 11.00 / kWh
     * Above 750 kWh: Rs. 13.50 / kWh
     * Fixed Demand Charge: Rs. 200.00 – Rs. 500.00 | Tax: 9.0%
  3. **Industrial Connection:**
     * 0 – 1000 kWh: Rs. 9.00 / kWh
     * Above 1000 kWh: Rs. 12.50 / kWh
     * Fixed Demand Charge: Rs. 800.00 – Rs. 1500.00 | Duty/Cess: 12.0%
  4. **Agricultural Connection (Subsidized):**
     * 0 – 500 kWh: Rs. 1.50 / kWh
     * Above 500 kWh: Rs. 2.80 / kWh
     * Fixed Demand Charge: Rs. 30.00 – Rs. 50.00 | Cess: 2.0%
* **Mathematical Calculation Logic:**
  $$\text{Energy Charges} = \sum_{i=1}^{n} (\text{Units in Slab } i \times \text{Rate}_i)$$
  $$\text{Tax Amount} = (\text{Energy Charges} + \text{Fixed Charge}) \times \text{Tax Rate}$$
  $$\text{Total Bill Payable} = \text{Energy Charges} + \text{Fixed Charge} + \text{Tax Amount}$$
* **Outputs:** Generated `Bill` record with unique invoice number (e.g., `BILL-202404-C101`), due date (+15 days), and `Unpaid` payment status.
* **Implementation:** `tariff_slabs` table, `BillingService.java`, Main Menu Option 5.

---

### FR-5: Consumer-Wise Billing & Official Invoice Generation
* **Description:** The system shall generate itemized electricity invoices for individual consumers for any selected billing period.
* **Invoice Particulars:**
  * Header: Electricity Distribution Board & Official Invoice Banner
  * Consumer Metadata: ID, Name, Address, Category, Phone, Meter Number
  * Period Data: Billing Month, Bill Date, Due Date (+15 days grace period)
  * Itemized Charges: Units consumed (kWh), Slab energy charge breakdown, Fixed demand charge, Tax/Cess, Total amount payable, Payment status.
* **Outputs:** Formatted terminal invoice printout and database persistence in `bills` table.
* **Implementation:** `ReportService.printConsumerBill()`, Main Menu Option 6.

---

### FR-6: Monthly Energy Consumption Audit & Aggregate Reporting
* **Description:** The system shall aggregate and analyze monthly energy consumption across all consumers and connection categories.
* **Audit Metrics Generated:**
  1. Total active meters audited in period.
  2. Gross electricity consumed (total kWh).
  3. Mean consumption per meter ($\overline{x} = \frac{\sum x}{n}$).
  4. Minimum and Peak (maximum) consumption recorded.
  5. Category-wise breakdown: meter count, cumulative kWh, and percentage share of total energy grid.
* **Outputs:** Tabular summary report in console and analytical SQL views.
* **Implementation:** `ReportService.printMonthlyConsumptionReport()`, SQL Query 4 & 5, Main Menu Option 7.

---

### FR-7: High-Consumption Outlier Identification & Multi-Level Alerting
* **Description:** The system shall automatically detect and rank consumers whose power consumption exceeds a defined threshold (default: $\ge 500\text{ kWh}$) to monitor grid stress and prevent commercial loss.
* **Severity Classification Hierarchy:**
  * **`[CRITICAL]`:** Usage $\ge 3000\text{ kWh}$ (Heavy industrial units, potential overload risk)
  * **`[SEVERE]`:** Usage between $1000\text{ kWh}$ and $2999\text{ kWh}$ (Heavy commercial / unmetered spikes)
  * **`[HIGH]`:** Usage between $500\text{ kWh}$ and $999\text{ kWh}$ (Significant domestic / seasonal peak)
* **Outputs:** Sorted descending alert list with consumer name, ID, category, units consumed, and alert severity.
* **Implementation:** `ReportService.printHighConsumptionReport()`, SQL Query 6 & 7, Main Menu Option 8.

---

### FR-8: Analytical Data Retrieval, Preprocessing & Visualization Engine
* **Description:** The system shall extract consumption telemetry from the database, preprocess it using Python, and generate graphical visual insights.
* **Processing Steps:**
  1. **Data Retrieval:** Connects to `electricity.db` via `sqlite3` and executes multi-table SQL join.
  2. **Preprocessing (Pandas):** Handles missing values, calculates rolling averages, computes Month-over-Month (MoM) growth percentage:
     $$\text{MoM Growth \%} = \frac{\text{Current Month Units} - \text{Previous Month Units}}{\text{Previous Month Units}} \times 100$$
  3. **Visualizations:**
     * Multi-line trend plots for seasonal consumption progression.
     * Category distribution donut charts and boxplots.
     * Top-consumer horizontal bar charts with threshold limit lines.
     * Interactive web dashboard (`index.html` & `energy_usage_dashboard.html`).
* **Implementation:** `python_analysis/energy_analysis.py`, `index.html`, Main Menu Option 10.

---

## 3. Requirements Traceability Matrix (RTM)

| Requirement ID | Requirement Summary | Java Component | DBMS Component | Python Component |
| :---: | :--- | :--- | :--- | :--- |
| **FR-1** | Maintain Consumer Details | `Consumer.java`, `ConsumerDAO.java` | `consumers` table | `energy_analysis.py` |
| **FR-2** | Record Previous & Current Readings | `MeterReading.java`, `MeterReadingDAO.java` | `meter_readings` table | `energy_analysis.py` |
| **FR-3** | Calculate Units (`Current - Prev`) | `MeterReading.calculateUnitsConsumed()` | `chk_readings` constraint | `queries.sql (Q2)` |
| **FR-4** | Tiered Tariff Slab Calculation | `BillingService.generateBillForReading()` | `tariff_slabs` table | `index.html (Calc)` |
| **FR-5** | Consumer-Wise Invoice Generation | `ReportService.printConsumerBill()` | `bills` table | `energy_analysis.py` |
| **FR-6** | Monthly Consumption Audit Report | `ReportService.printMonthlyReport()` | `queries.sql (Q4, Q5)` | `energy_analysis.py` |
| **FR-7** | High-Consumption Outlier Alerts | `ReportService.printHighUsageReport()` | `queries.sql (Q6, Q7)` | `1_monthly_trend.png` |
| **FR-8** | Visual Analytics & Preprocessing | `ReportService.exportDataToCSV()` | `electricity.db` | `energy_analysis.py` |
