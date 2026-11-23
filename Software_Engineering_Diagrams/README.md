# Software Engineering Diagrams
## Scenario 14: Electricity Consumption Management and Energy Usage Analysis System

**Register Number:** 732925ITR104  
**Academic Record:** B.Tech Software Engineering Project Presentation  
**Technology Architecture:** Java (Core/JDBC), DBMS (MySQL/SQLite), Python (Pandas & Visualization)

---

### Deliverables Index

| # | Diagram Type | PNG Image | Editable Sources | Description |
|---|---|---|---|---|
| 1 | **Use Case Diagram** | [`Use_Case_Diagram.png`](Use_Case_Diagram.png) | [PlantUML](Use_Case_Diagram.puml) / [Mermaid](Use_Case_Diagram.mmd) | Actors (Admin, Consumer, Data Analyst), System Boundary, 13 core use cases with `<<include>>` and `<<extend>>`. |
| 2 | **Class Diagram** | [`Class_Diagram.png`](Class_Diagram.png) | [PlantUML](Class_Diagram.puml) / [Mermaid](Class_Diagram.mmd) | 7 Classes (Consumer, MeterReading, ElectricityBill, Tariff, ConsumptionReport, Database, DataAnalysis) with attributes, methods, visibilities, and multiplicities. |
| 3 | **Activity Diagram** | [`Activity_Diagram.png`](Activity_Diagram.png) | [PlantUML](Activity_Diagram.puml) / [Mermaid](Activity_Diagram.mmd) | Complete operational workflow from registration to meter audits, tiered billing, and Python analytical dashboards. |
| 4 | **Sequence Diagram** | [`Sequence_Diagram.png`](Sequence_Diagram.png) | [PlantUML](Sequence_Diagram.puml) / [Mermaid](Sequence_Diagram.mmd) | Chronological message sequence across Admin, Java UI, Business Modules, Database, and Python Analysis engine. |
| 5 | **DFD Level 0 (Context)** | [`DFD_Level_0.png`](DFD_Level_0.png) | [PlantUML](DFD_Level_0.puml) / [Mermaid](DFD_Level_0.mmd) | High-level context diagram showing External Entities and main System boundary data inflows and outflows. |
| 6 | **DFD Level 1** | [`DFD_Level_1.png`](DFD_Level_1.png) | [PlantUML](DFD_Level_1.puml) / [Mermaid](DFD_Level_1.mmd) | Functional decomposition with 5 Sub-Processes (1.0 - 5.0) and 3 Data Stores (D1 Consumer, D2 Meter Reading, D3 Billing). |
| 7 | **ER Diagram** | [`ER_Diagram.png`](ER_Diagram.png) | [PlantUML](ER_Diagram.puml) / [Mermaid](ER_Diagram.mmd) | Relational database schema with 5 entities, Primary Keys (PK), Foreign Keys (FK), and exact 1:N & 1:1 cardinalities. |

---

### Combined Academic Presentation Document
* **Single Combined PDF Document:** [**`Scenario_14_SE_Diagrams.pdf`**](Scenario_14_SE_Diagrams.pdf)
  * Format: High-Resolution A4 Landscape
  * Pages: 7 standalone pages (one diagram per page) with title headers, metadata, and formal academic formatting.

---

### Technical Highlights
1. **Consistency:** All 7 diagrams share identical entity names, class signatures, attribute types, and process identifiers.
2. **Standard Notations:** UML 2.5, Gane & Sarson DFD symbols, and Relational ER notation.
3. **Color Palette:** Clean white canvas (`#ffffff`), dark blue headers (`#1e3c72`), electric blue accents (`#2a5298`), amber decision points (`#d97706`), and audit alert red (`#dc2626`).