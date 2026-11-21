# Software Engineering (SE) Requirements Documentation
**Project:** Electricity Consumption Management and Energy Usage Analysis System  
**Scenario:** Scenario 14  
**Student Register Number:** 732925ITR104  
**Technology Stack:** Java (Core/JDBC), DBMS (MySQL/SQLite), Python (Pandas/Matplotlib/Seaborn)  

---

## 1. Overview of the `SE/` Folder
This directory houses the formal Software Engineering (SE) requirements specifications for the Electricity Consumption Management System, prepared in accordance with IEEE 830 and ISO/IEC 25010 standards.

It is structured into two core specification documents:

1. 📄 **[`FUNCTIONAL_REQUIREMENTS.md`](./FUNCTIONAL_REQUIREMENTS.md)**:
   * **FR-1:** Consumer Master Record & Account Category Management (CRUD, validations, domains)
   * **FR-2:** Meter Reading Telemetry Recording (Previous & Current readings, period YYYY-MM, timestamps)
   * **FR-3:** Electricity Consumption Calculation (`Units = Current - Previous`, non-negative constraints)
   * **FR-4:** Tiered Tariff Slab & Multi-Charge Billing Engine (Domestic, Commercial, Industrial, Agricultural)
   * **FR-5:** Consumer-Wise Invoicing & Line-Item Breakdown Generation
   * **FR-6:** Monthly Energy Consumption Audit & Aggregate Reporting
   * **FR-7:** High-Consumption Outlier Identification & Severity Alert Classification (`CRITICAL`, `SEVERE`, `HIGH`)
   * **FR-8:** Analytical Data Retrieval, Preprocessing & Visualization Engine (SQLite/Pandas/Matplotlib)
   * **RTM:** Requirements Traceability Matrix linking requirements to source code.

2. 📄 **[`NON_FUNCTIONAL_REQUIREMENTS.md`](./NON_FUNCTIONAL_REQUIREMENTS.md)**:
   * **NFR-1:** Performance & Scalability (latency < 150ms, B-Tree database indexing, high throughput)
   * **NFR-2:** Reliability & Fault Tolerance (ACID compliance, referential cascading integrity, fallback persistence)
   * **NFR-3:** Data Integrity & Accuracy (Two decimal currency precision, non-negative reading checks)
   * **NFR-4:** Security & Input Robustness (Parameterized `PreparedStatement` SQL queries, input sanitization)
   * **NFR-5:** Maintainability & Modularity (Layered MVC/DAO pattern, decoupled Service logic)
   * **NFR-6:** Usability & User Experience (Interactive 10-option CLI console, responsive live web dashboard)
   * **NFR-7:** Portability & Extensibility (Dual MySQL/SQLite support, Windows `.bat` and Linux `.sh` support)

---

## 2. Quick Reference Summary Table

```
+-------------------------------------------------------------------------------------------------------+
|  Req Code | Requirement Title                    | Category    | Primary Implementation Component     |
+-------------------------------------------------------------------------------------------------------+
|  FR-1     | Consumer Details Maintenance         | Functional  | Consumer.java, ConsumerDAO.java      |
|  FR-2     | Previous & Current Meter Readings    | Functional  | MeterReading.java, MeterReadingDAO   |
|  FR-3     | Consumption Calculation (Current-Prev| Functional  | calculateUnitsConsumed(), SQL check  |
|  FR-4     | Predefined Tariff Slab Billing       | Functional  | BillingService.java, tariff_slabs    |
|  FR-5     | Consumer-Wise Invoices               | Functional  | ReportService.printConsumerBill()    |
|  FR-6     | Monthly Aggregate Consumption Report | Functional  | ReportService.printMonthlyReport()   |
|  FR-7     | High-Consumption Outlier Alerts      | Functional  | ReportService.printHighUsageReport() |
|  FR-8     | Python Data Retrieval & Visuals      | Functional  | energy_analysis.py, index.html       |
+-----------+--------------------------------------+-------------+--------------------------------------+
|  NFR-1    | Performance & Low Latency            | Quality     | B-Tree SQL Indexes, Pandas vector    |
|  NFR-2    | Reliability & ACID Fallback Store    | Quality     | DBConnection.java fallback           |
|  NFR-3    | Financial Precision & Non-Negative   | Quality     | DECIMAL(10,2), Math.round precision  |
|  NFR-4    | SQL Injection Prevention             | Security    | Parameterized PreparedStatement      |
|  NFR-5    | 4-Tier Layered Architecture          | Modularity  | Model -> DAO -> Service -> UI        |
|  NFR-6    | Interactive Console & Web UX         | Usability   | Main.java (CLI), GitHub Pages (Web)  |
|  NFR-7    | Cross-Platform Runnability           | Portability | Java 8-25, MySQL & SQLite, Win/Linux |
+-------------------------------------------------------------------------------------------------------+
```
