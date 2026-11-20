# Part 2: Java Application Presentation & Evaluation Guide
**Scenario 14: Electricity Consumption Management System**  
**Student Register Number: 732925ITR104**

---

## 1. Objective of the Java Module
To implement an enterprise-grade Object-Oriented Management System that:
1. Encapsulates entities (`Consumer`, `MeterReading`, `TariffSlab`, `Bill`).
2. Provides a decoupled DAO (Data Access Object) layer with JDBC database connectivity and fallback persistence.
3. Implements business logic for progressive tiered tariff slab calculations and automated consumption computation (`current - previous`).
4. Generates formatted consumer invoices, monthly audit reports, and high-consumption alert lists.

---

## 2. Architecture & Design Patterns

The application follows the industry-standard **Layered MVC/DAO Architecture**:

```
[Main.java (Console UI)]
         |
         v
[Service Layer] --------> BillingService.java  (Tiered Tariff Logic)
                --------> ReportService.java   (Invoices & Audit Reports)
         |
         v
[DAO Layer] ------------> ConsumerDAO.java, MeterReadingDAO.java, BillDAO.java
         |
         v
[DBConnection.java] ----> JDBC Driver (SQLite / MySQL) / Built-in Persistent Store
         |
         v
[Model Layer] ----------> Consumer, MeterReading, TariffSlab, Bill (POJOs)
```

---

## 3. Key Classes & Code Highlights to Show the Professor

### 1. Calculation of Consumption (`current - previous`)
In [`MeterReading.java`](../java_app/src/com/electricity/model/MeterReading.java):
```java
public double calculateUnitsConsumed() {
    if (currentReading < previousReading) {
        throw new IllegalArgumentException("Current reading cannot be less than previous reading.");
    }
    return Math.round((currentReading - previousReading) * 100.0) / 100.0;
}
```

### 2. Progressive Tiered Tariff Calculation Logic
In [`BillingService.java`](../java_app/src/com/electricity/service/BillingService.java):
```java
switch (connType.trim()) {
    case "Domestic":
        taxRate = 0.05; // 5% GST
        if (units <= 100) {
            energyCharges = units * 3.50;
            fixedCharges = 50.00;
        } else if (units <= 300) {
            energyCharges = (100 * 3.50) + ((units - 100) * 5.20);
            fixedCharges = 75.00;
        } else if (units <= 500) {
            energyCharges = (100 * 3.50) + (200 * 5.20) + ((units - 300) * 7.50);
            fixedCharges = 100.00;
        } else {
            energyCharges = (100 * 3.50) + (200 * 5.20) + (200 * 7.50) + ((units - 500) * 9.80);
            fixedCharges = 150.00;
        }
        break;
    // Similar progressive slabs for Commercial, Industrial, Agricultural...
}
```

---

## 4. How to Run the Live Demo for the Professor

### Step 1: Open Terminal in the `java_app` folder
```cmd
cd java_app
compile_and_run.bat
```
*(On Linux/macOS, run `./run.sh`)*

### Step 2: Live Demo Sequence

1. **Option 2 (View All Consumers):**
   * Shows all active consumer accounts across Domestic, Commercial, Industrial, and Agricultural categories.
2. **Option 3 (Record Meter Reading):**
   * Select an existing consumer (e.g. `C101`).
   * Enter a current reading (e.g. `2250.0`).
   * Show that the system automatically subtracts the previous reading and displays:  
     `Units Consumed: 255.00 kWh (Current - Previous)`.
3. **Option 4 (Calculate & Generate Bill):**
   * Select reading ID to calculate.
   * Shows the computed energy charges, fixed charges, taxes, and total payable amount.
4. **Option 5 (View Consumer Invoice):**
   * Enter `C101`. Displays the official formatted invoice with line-item breakdown.
5. **Option 6 (Monthly Consumption Audit Report):**
   * Displays overall statistics (total kWh, average, min, peak reading, and percentage share per sector).
6. **Option 7 (List High-Consumption Consumers):**
   * Enter threshold `500`. Shows all flagged high consumers categorized as `[CRITICAL]`, `[SEVERE]`, or `[HIGH]`.

---

## 5. Typical Java Viva Questions & Model Answers

* **Q: Which OOP principles did you apply in this project?**  
  *A:*
  * **Encapsulation:** Model classes protect private instance variables (`consumerId`, `currentReading`) with public getters/setters and validation in `calculateUnitsConsumed()`.
  * **Separation of Concerns:** Distinct separation between Presentation (`Main`), Business Logic (`BillingService`), Data Access (`DAOs`), and Domain Models (`POJOs`).

* **Q: Why is the DAO pattern used instead of putting SQL in `Main.java`?**  
  *A:* DAO abstracts the underlying persistence mechanism. If the database changes from SQLite to MySQL or PostgreSQL, only the DAO/DBConnection classes are modified, leaving business logic and UI untouched.

* **Q: How does `BillingService` calculate progressive slabs rather than flat rates?**  
  *A:* In progressive slab systems, each unit of energy consumed is charged at the rate of the specific band it falls into, ensuring fair billing for basic lifelines while penalizing excessive usage.
