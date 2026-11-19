package com.electricity;

import com.electricity.dao.BillDAO;
import com.electricity.dao.ConsumerDAO;
import com.electricity.dao.MeterReadingDAO;
import com.electricity.model.Bill;
import com.electricity.model.Consumer;
import com.electricity.model.MeterReading;
import com.electricity.service.BillingService;
import com.electricity.service.ReportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Scenario 14: Electricity Consumption Management and Energy Usage Analysis System
 * Main Console User Interface
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ConsumerDAO consumerDAO = new ConsumerDAO();
    private static final MeterReadingDAO meterReadingDAO = new MeterReadingDAO();
    private static final BillDAO billDAO = new BillDAO();
    private static final BillingService billingService = new BillingService(consumerDAO, meterReadingDAO, billDAO);
    private static final ReportService reportService = new ReportService();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            printBanner();
            printMenu();
            System.out.print(" Enter your choice (0-9): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    handleRegisterConsumer();
                    break;
                case "2":
                    handleViewAllConsumers();
                    break;
                case "3":
                    handleRecordMeterReading();
                    break;
                case "4":
                    handleGenerateBill();
                    break;
                case "5":
                    handleViewConsumerBill();
                    break;
                case "6":
                    handleMonthlyConsumptionReport();
                    break;
                case "7":
                    handleHighConsumptionConsumers();
                    break;
                case "8":
                    handleViewTariffSlabs();
                    break;
                case "9":
                    handleExportData();
                    break;
                case "0":
                    System.out.println("\n[!] Thank you for using the Electricity Management System. Exiting...");
                    running = false;
                    break;
                default:
                    System.out.println("\n[X] Invalid selection. Please choose an option from 0 to 9.");
            }

            if (running) {
                System.out.print("\nPress ENTER to continue...");
                scanner.nextLine();
            }
        }
    }

    private static void printBanner() {
        System.out.println("\n===============================================================================");
        System.out.println("  ⚡ ELECTRICITY CONSUMPTION MANAGEMENT & ENERGY USAGE ANALYSIS SYSTEM ⚡     ");
        System.out.println("                 Scenario 14 | Register No.: 732925ITR104                       ");
        System.out.println("===============================================================================");
    }

    private static void printMenu() {
        System.out.println(" 1. Register New Consumer Detail               (Req 1)");
        System.out.println(" 2. View All Consumer Master Records           (Req 1)");
        System.out.println(" 3. Record Meter Reading & Compute Consumption (Req 2 & 3)");
        System.out.println(" 4. Calculate & Generate Electricity Bill      (Req 4)");
        System.out.println(" 5. View Consumer-Wise Bill / Invoice          (Req 5)");
        System.out.println(" 6. Generate Monthly Consumption Audit Report  (Req 5)");
        System.out.println(" 7. List High-Consumption Consumers (Threshold)(Req 5)");
        System.out.println(" 8. Display Predefined Tariff Slabs & Rates    (Req 4)");
        System.out.println(" 9. Export Analytical Dataset for Python Visuals(Req 6)");
        System.out.println(" 0. Exit System");
        System.out.println("-------------------------------------------------------------------------------");
    }

    // 1. Maintain Consumer Details
    private static void handleRegisterConsumer() {
        System.out.println("\n--- [Register New Consumer] ---");
        System.out.print("Enter Consumer ID (e.g. C106): ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("[X] Consumer ID cannot be empty.");
            return;
        }

        if (consumerDAO.getConsumerById(id) != null) {
            System.out.println("[X] Consumer with ID " + id + " already exists!");
            return;
        }

        System.out.print("Enter Consumer Full Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Premise Address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Enter Contact Phone: ");
        String phone = scanner.nextLine().trim();

        System.out.println("Select Connection Type:");
        System.out.println(" 1. Domestic\n 2. Commercial\n 3. Industrial\n 4. Agricultural");
        System.out.print("Enter Choice (1-4): ");
        String catChoice = scanner.nextLine().trim();
        String connType = "Domestic";
        switch (catChoice) {
            case "2": connType = "Commercial"; break;
            case "3": connType = "Industrial"; break;
            case "4": connType = "Agricultural"; break;
            default: connType = "Domestic"; break;
        }

        System.out.print("Enter Meter Serial Number: ");
        String meterNo = scanner.nextLine().trim();
        if (meterNo.isEmpty()) {
            meterNo = "MTR-" + connType.substring(0, 3).toUpperCase() + "-" + id;
        }

        Consumer c = new Consumer(id, name, address, phone, connType, meterNo, LocalDate.now(), "Active");
        consumerDAO.addConsumer(c);
        System.out.println("\n[✓] Consumer registered successfully: " + c.getName() + " (" + c.getConnectionType() + ")");
    }

    // 2. View All Consumers
    private static void handleViewAllConsumers() {
        System.out.println("\n--- [All Registered Consumers] ---");
        List<Consumer> consumers = consumerDAO.getAllConsumers();
        System.out.printf(" %-8s | %-22s | %-13s | %-14s | %-12s | %s%n", 
                "ID", "Name", "Category", "Meter No", "Phone", "Status");
        System.out.println(" -----------------------------------------------------------------------------------------");
        for (Consumer c : consumers) {
            System.out.printf(" %-8s | %-22s | %-13s | %-14s | %-12s | %s%n",
                    c.getConsumerId(),
                    (c.getName().length() > 22 ? c.getName().substring(0, 19) + "..." : c.getName()),
                    c.getConnectionType(),
                    c.getMeterNumber(),
                    c.getPhone(),
                    c.getStatus());
        }
        System.out.println(" Total Registered Consumers: " + consumers.size());
    }

    // 3. Record Meter Reading & Calculate Consumption (Req 2 & 3)
    private static void handleRecordMeterReading() {
        System.out.println("\n--- [Record Meter Reading] ---");
        System.out.print("Enter Consumer ID: ");
        String id = scanner.nextLine().trim();
        Consumer c = consumerDAO.getConsumerById(id);
        if (c == null) {
            System.out.println("[X] Consumer not found: " + id);
            return;
        }

        System.out.println("Found: " + c.getName() + " | Category: " + c.getConnectionType() + " | Meter: " + c.getMeterNumber());
        
        MeterReading latest = meterReadingDAO.getLatestReading(id);
        double suggestedPrev = (latest != null) ? latest.getCurrentReading() : 0.0;

        System.out.print("Enter Billing Month (e.g., 2024-05): ");
        String month = scanner.nextLine().trim();
        if (month.isEmpty()) {
            month = LocalDate.now().toString().substring(0, 7);
        }

        System.out.print("Enter Previous Reading [Default: " + suggestedPrev + "]: ");
        String prevStr = scanner.nextLine().trim();
        double prevReading = prevStr.isEmpty() ? suggestedPrev : Double.parseDouble(prevStr);

        System.out.print("Enter Current Reading: ");
        double currReading = Double.parseDouble(scanner.nextLine().trim());

        if (currReading < prevReading) {
            System.out.println("[X] Validation Error: Current reading (" + currReading + ") cannot be less than Previous (" + prevReading + ")!");
            return;
        }

        // Functional Requirement 3: Units = Current - Previous
        double units = currReading - prevReading;
        MeterReading reading = new MeterReading(0, id, month, LocalDate.now(), prevReading, currReading, units);
        meterReadingDAO.recordReading(reading);

        System.out.println("\n[✓] Reading recorded successfully!");
        System.out.printf("    Previous Reading : %10.2f kWh%n", prevReading);
        System.out.printf("    Current Reading  : %10.2f kWh%n", currReading);
        System.out.printf("    Units Consumed   : %10.2f kWh (Current - Previous)%n", units);
    }

    // 4. Calculate & Generate Electricity Bill (Req 4)
    private static void handleGenerateBill() {
        System.out.println("\n--- [Calculate & Generate Electricity Bill] ---");
        System.out.print("Enter Consumer ID: ");
        String id = scanner.nextLine().trim();

        List<MeterReading> readings = meterReadingDAO.getReadingsByConsumer(id);
        if (readings.isEmpty()) {
            System.out.println("[X] No meter readings recorded for consumer " + id);
            return;
        }

        System.out.println("Available readings for " + id + ":");
        for (MeterReading r : readings) {
            System.out.printf(" Reading #%-4d | Month: %s | Units: %8.2f kWh%n", 
                    r.getReadingId(), r.getBillingMonth(), r.getUnitsConsumed());
        }

        System.out.print("Enter Reading ID to Bill: ");
        int readingId = Integer.parseInt(scanner.nextLine().trim());

        try {
            Bill bill = billingService.generateBillForReading(readingId);
            System.out.println("\n[✓] Bill Generated Successfully!");
            System.out.printf(" Bill ID       : %s%n", bill.getBillId());
            System.out.printf(" Units Consumed: %.2f kWh%n", bill.getUnitsConsumed());
            System.out.printf(" Energy Charges: Rs. %.2f%n", bill.getEnergyCharges());
            System.out.printf(" Fixed Charges : Rs. %.2f%n", bill.getFixedCharges());
            System.out.printf(" Taxes (Duty)  : Rs. %.2f%n", bill.getTaxAmount());
            System.out.printf(" Total Payable : Rs. %.2f%n", bill.getTotalAmount());
            System.out.printf(" Due Date      : %s%n", bill.getDueDate());
        } catch (Exception e) {
            System.out.println("[X] Failed to generate bill: " + e.getMessage());
        }
    }

    // 5. View Consumer-Wise Bill (Req 5.1)
    private static void handleViewConsumerBill() {
        System.out.print("\nEnter Consumer ID (e.g. C101): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Billing Month (leave blank for latest): ");
        String month = scanner.nextLine().trim();
        if (month.isEmpty()) month = null;

        reportService.printConsumerBill(id, month);
    }

    // 6. Generate Monthly Consumption Report (Req 5.2)
    private static void handleMonthlyConsumptionReport() {
        System.out.print("\nEnter Billing Month (e.g. 2024-04 or press ENTER for all months): ");
        String month = scanner.nextLine().trim();
        reportService.printMonthlyConsumptionReport(month.isEmpty() ? null : month);
    }

    // 7. List High-Consumption Consumers (Req 5.3)
    private static void handleHighConsumptionConsumers() {
        System.out.print("\nEnter High Consumption Threshold in Units [Default 500]: ");
        String input = scanner.nextLine().trim();
        double threshold = input.isEmpty() ? 500.0 : Double.parseDouble(input);
        reportService.printHighConsumptionReport(threshold);
    }

    // 8. View Tariff Slabs
    private static void handleViewTariffSlabs() {
        System.out.println("\n==========================================================================");
        System.out.println("                       PREDEFINED TARIFF RATE CARDS                       ");
        System.out.println("==========================================================================");
        System.out.println(" 1. Domestic Category:");
        System.out.println("    - 0 to 100 units       : Rs. 3.50 / unit | Fixed: Rs.  50.00 | Tax: 5%");
        System.out.println("    - 101 to 300 units     : Rs. 5.20 / unit | Fixed: Rs.  75.00 | Tax: 5%");
        System.out.println("    - 301 to 500 units     : Rs. 7.50 / unit | Fixed: Rs. 100.00 | Tax: 5%");
        System.out.println("    - Above 500 units      : Rs. 9.80 / unit | Fixed: Rs. 150.00 | Tax: 5%");
        System.out.println("\n 2. Commercial Category:");
        System.out.println("    - 0 to 200 units       : Rs. 8.50 / unit | Fixed: Rs. 200.00 | Tax: 9%");
        System.out.println("    - 201 to 750 units     : Rs. 11.00 / unit | Fixed: Rs. 350.00 | Tax: 9%");
        System.out.println("    - Above 750 units      : Rs. 13.50 / unit | Fixed: Rs. 500.00 | Tax: 9%");
        System.out.println("\n 3. Industrial Category:");
        System.out.println("    - 0 to 1000 units      : Rs. 9.00 / unit | Fixed: Rs. 800.00 | Tax: 12%");
        System.out.println("    - Above 1000 units     : Rs. 12.50 / unit | Fixed: Rs. 1500.00 | Tax: 12%");
        System.out.println("\n 4. Agricultural Category:");
        System.out.println("    - 0 to 500 units       : Rs. 1.50 / unit | Fixed: Rs.  30.00 | Tax: 2%");
        System.out.println("    - Above 500 units      : Rs. 2.80 / unit | Fixed: Rs.  50.00 | Tax: 2%");
        System.out.println("==========================================================================");
    }

    // 9. Export Analytical Dataset
    private static void handleExportData() {
        String path = "../database/energy_data.csv";
        reportService.exportDataToCSV(path);
    }
}
