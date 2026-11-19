package com.electricity.service;

import com.electricity.dao.BillDAO;
import com.electricity.dao.ConsumerDAO;
import com.electricity.dao.MeterReadingDAO;
import com.electricity.model.Bill;
import com.electricity.model.Consumer;
import com.electricity.model.MeterReading;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.*;

/**
 * Service to generate reports and consumer bills.
 * Functional Requirement 5: Generate consumer-wise bills, monthly consumption reports, 
 * and lists of high-consumption consumers.
 */
public class ReportService {
    private final ConsumerDAO consumerDAO;
    private final MeterReadingDAO meterReadingDAO;
    private final BillDAO billDAO;

    public ReportService() {
        this.consumerDAO = new ConsumerDAO();
        this.meterReadingDAO = new MeterReadingDAO();
        this.billDAO = new BillDAO();
    }

    /**
     * Requirement 5.1: Generate and display Consumer-wise Bill
     */
    public void printConsumerBill(String consumerId, String month) {
        Consumer consumer = consumerDAO.getConsumerById(consumerId);
        if (consumer == null) {
            System.out.println("[Error] Consumer not found with ID: " + consumerId);
            return;
        }

        List<Bill> bills = billDAO.getBillsByConsumer(consumerId);
        Bill selectedBill = null;
        for (Bill b : bills) {
            if (month == null || b.getBillingMonth().equalsIgnoreCase(month)) {
                selectedBill = b;
                break;
            }
        }

        if (selectedBill == null) {
            System.out.println("[Notice] No bill record found for Consumer " + consumerId + (month != null ? " in " + month : ""));
            return;
        }

        System.out.println("\n==========================================================================");
        System.out.println("                   ⚡ ELECTRICITY DISTRIBUTION BOARD ⚡                   ");
        System.out.println("                         OFFICIAL CONSUMER INVOICE                         ");
        System.out.println("==========================================================================");
        System.out.printf(" Bill Number     : %-25s Bill Date  : %s%n", selectedBill.getBillId(), selectedBill.getBillDate());
        System.out.printf(" Billing Month   : %-25s Due Date   : %s%n", selectedBill.getBillingMonth(), selectedBill.getDueDate());
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(" Consumer ID     : %-25s Category   : %s%n", consumer.getConsumerId(), consumer.getConnectionType());
        System.out.printf(" Consumer Name   : %-25s Meter No   : %s%n", consumer.getName(), consumer.getMeterNumber());
        System.out.printf(" Address         : %-45s%n", consumer.getAddress());
        System.out.printf(" Contact Phone   : %-45s%n", consumer.getPhone());
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(" PARTICULARS & CHARGES BREAKDOWN                                AMOUNT (Rs)");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(" Units Consumed (kWh)            : %12.2f units%n", selectedBill.getUnitsConsumed());
        System.out.printf(" Energy Charges (Tariff Slabs)   :                     %12.2f%n", selectedBill.getEnergyCharges());
        System.out.printf(" Fixed Demand Charges            :                     %12.2f%n", selectedBill.getFixedCharges());
        System.out.printf(" Electricity Duty / Tax / Cess   :                     %12.2f%n", selectedBill.getTaxAmount());
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf(" TOTAL AMOUNT PAYABLE            :                 Rs. %12.2f%n", selectedBill.getTotalAmount());
        System.out.printf(" Payment Status                  :                     %12s%n", selectedBill.getPaymentStatus().toUpperCase());
        System.out.println("==========================================================================\n");
    }

    /**
     * Requirement 5.2: Generate Monthly Consumption Report
     */
    public void printMonthlyConsumptionReport(String month) {
        List<MeterReading> readings = (month != null && !month.trim().isEmpty()) 
                ? meterReadingDAO.getReadingsByMonth(month.trim()) 
                : meterReadingDAO.getAllReadings();

        if (readings.isEmpty()) {
            System.out.println("[Notice] No consumption records found for period: " + (month != null ? month : "ALL"));
            return;
        }

        double totalUnits = 0;
        double minUnits = Double.MAX_VALUE;
        double maxUnits = Double.MIN_VALUE;

        Map<String, Double> categoryUnits = new HashMap<>();
        Map<String, Integer> categoryCounts = new HashMap<>();

        for (MeterReading r : readings) {
            double u = r.getUnitsConsumed();
            totalUnits += u;
            if (u < minUnits) minUnits = u;
            if (u > maxUnits) maxUnits = u;

            Consumer c = consumerDAO.getConsumerById(r.getConsumerId());
            String cat = (c != null) ? c.getConnectionType() : "Unknown";
            categoryUnits.put(cat, categoryUnits.getOrDefault(cat, 0.0) + u);
            categoryCounts.put(cat, categoryCounts.getOrDefault(cat, 0) + 1);
        }

        double avgUnits = totalUnits / readings.size();

        System.out.println("\n==========================================================================");
        System.out.println("                     MONTHLY CONSUMPTION AUDIT REPORT                     ");
        System.out.println("                          Period: " + (month != null ? month : "All Recorded Periods"));
        System.out.println("==========================================================================");
        System.out.printf(" Total Meter Readings Recorded   : %d%n", readings.size());
        System.out.printf(" Total Electricity Consumed      : %.2f kWh (Units)%n", totalUnits);
        System.out.printf(" Average Consumption per Meter   : %.2f kWh (Units)%n", avgUnits);
        System.out.printf(" Minimum Consumption Recorded    : %.2f kWh (Units)%n", minUnits);
        System.out.printf(" Peak (Maximum) Meter Reading    : %.2f kWh (Units)%n", maxUnits);
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(" CATEGORY-WISE BREAKDOWN:");
        System.out.printf(" %-16s | %-12s | %-16s | %-14s%n", "Category", "Consumer Ct", "Total Units (kWh)", "Share of Total");
        System.out.println(" -------------------------------------------------------------------------");
        for (Map.Entry<String, Double> entry : categoryUnits.entrySet()) {
            String cat = entry.getKey();
            double units = entry.getValue();
            int count = categoryCounts.get(cat);
            double pct = (units / totalUnits) * 100.0;
            System.out.printf(" %-16s | %-12d | %16.2f | %13.2f%%%n", cat, count, units, pct);
        }
        System.out.println("==========================================================================\n");
    }

    /**
     * Requirement 5.3: Lists of High-Consumption Consumers
     */
    public void printHighConsumptionReport(double thresholdUnits) {
        List<MeterReading> readings = meterReadingDAO.getAllReadings();
        List<MeterReading> highConsumers = new ArrayList<>();

        for (MeterReading r : readings) {
            if (r.getUnitsConsumed() >= thresholdUnits) {
                highConsumers.add(r);
            }
        }

        highConsumers.sort((a, b) -> Double.compare(b.getUnitsConsumed(), a.getUnitsConsumed()));

        System.out.println("\n==========================================================================");
        System.out.println("                     HIGH-CONSUMPTION CONSUMERS ALERT                     ");
        System.out.println("                 Threshold: >= " + thresholdUnits + " kWh Units");
        System.out.println("==========================================================================");
        System.out.printf(" %-8s | %-18s | %-12s | %-7s | %-10s | %-10s%n", 
                "ID", "Name", "Category", "Month", "Units (kWh)", "Alert Level");
        System.out.println("--------------------------------------------------------------------------");

        if (highConsumers.isEmpty()) {
            System.out.println(" No consumers exceeded the threshold of " + thresholdUnits + " units.");
        } else {
            for (MeterReading r : highConsumers) {
                Consumer c = consumerDAO.getConsumerById(r.getConsumerId());
                String name = (c != null) ? c.getName() : "Unknown";
                String cat = (c != null) ? c.getConnectionType() : "Unknown";
                String alertLevel;
                if (r.getUnitsConsumed() >= 3000) {
                    alertLevel = "[CRITICAL]";
                } else if (r.getUnitsConsumed() >= 1000) {
                    alertLevel = "[SEVERE]";
                } else {
                    alertLevel = "[HIGH]";
                }

                System.out.printf(" %-8s | %-18s | %-12s | %-7s | %10.2f | %-10s%n",
                        r.getConsumerId(),
                        (name.length() > 18 ? name.substring(0, 15) + "..." : name),
                        cat,
                        r.getBillingMonth(),
                        r.getUnitsConsumed(),
                        alertLevel);
            }
        }
        System.out.println("==========================================================================\n");
    }

    /**
     * Requirement 6 Helper: Export all data to CSV so Python can readily visualize it.
     */
    public void exportDataToCSV(String filePath) {
        List<MeterReading> readings = meterReadingDAO.getAllReadings();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("consumer_id,name,connection_type,address,meter_number,reading_id,billing_month,reading_date,previous_reading,current_reading,units_consumed,energy_charges,fixed_charges,tax_amount,bill_total,payment_status");

            for (MeterReading mr : readings) {
                Consumer c = consumerDAO.getConsumerById(mr.getConsumerId());
                Bill b = null;
                for (Bill bill : billDAO.getAllBills()) {
                    if (bill.getReadingId() == mr.getReadingId()) {
                        b = bill;
                        break;
                    }
                }

                String name = (c != null) ? c.getName() : "Unknown";
                String cat = (c != null) ? c.getConnectionType() : "Domestic";
                String addr = (c != null) ? c.getAddress().replace(",", ";") : "";
                String meter = (c != null) ? c.getMeterNumber() : "";

                double ec = (b != null) ? b.getEnergyCharges() : 0.0;
                double fc = (b != null) ? b.getFixedCharges() : 0.0;
                double tax = (b != null) ? b.getTaxAmount() : 0.0;
                double tot = (b != null) ? b.getTotalAmount() : 0.0;
                String status = (b != null) ? b.getPaymentStatus() : "Unbilled";

                writer.printf("%s,\"%s\",%s,\"%s\",%s,%d,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s%n",
                        mr.getConsumerId(), name, cat, addr, meter, mr.getReadingId(),
                        mr.getBillingMonth(), mr.getReadingDate(), mr.getPreviousReading(),
                        mr.getCurrentReading(), mr.getUnitsConsumed(), ec, fc, tax, tot, status);
            }
            System.out.println("[Success] Analytical dataset successfully exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("[Error] Failed to export CSV: " + e.getMessage());
        }
    }
}
