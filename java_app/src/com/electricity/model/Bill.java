package com.electricity.model;

import java.time.LocalDate;

/**
 * Model class representing a generated Electricity Bill.
 */
public class Bill {
    private String billId;
    private int readingId;
    private String consumerId;
    private String billingMonth;
    private double unitsConsumed;
    private double energyCharges;
    private double fixedCharges;
    private double taxAmount;
    private double totalAmount;
    private LocalDate billDate;
    private LocalDate dueDate;
    private String paymentStatus; // Paid, Unpaid, Overdue

    public Bill() {
        this.billDate = LocalDate.now();
        this.dueDate = LocalDate.now().plusDays(15);
        this.paymentStatus = "Unpaid";
    }

    public Bill(String billId, int readingId, String consumerId, String billingMonth,
                double unitsConsumed, double energyCharges, double fixedCharges,
                double taxAmount, double totalAmount, LocalDate billDate, LocalDate dueDate, String paymentStatus) {
        this.billId = billId;
        this.readingId = readingId;
        this.consumerId = consumerId;
        this.billingMonth = billingMonth;
        this.unitsConsumed = unitsConsumed;
        this.energyCharges = energyCharges;
        this.fixedCharges = fixedCharges;
        this.taxAmount = taxAmount;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.dueDate = dueDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public String getBillId() { return billId; }
    public void setBillId(String billId) { this.billId = billId; }

    public int getReadingId() { return readingId; }
    public void setReadingId(int readingId) { this.readingId = readingId; }

    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public String getBillingMonth() { return billingMonth; }
    public void setBillingMonth(String billingMonth) { this.billingMonth = billingMonth; }

    public double getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(double unitsConsumed) { this.unitsConsumed = unitsConsumed; }

    public double getEnergyCharges() { return energyCharges; }
    public void setEnergyCharges(double energyCharges) { this.energyCharges = energyCharges; }

    public double getFixedCharges() { return fixedCharges; }
    public void setFixedCharges(double fixedCharges) { this.fixedCharges = fixedCharges; }

    public double getTaxAmount() { return taxAmount; }
    public void setTaxAmount(double taxAmount) { this.taxAmount = taxAmount; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate billDate) { this.billDate = billDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    @Override
    public String toString() {
        return String.format("Bill: %-16s | Consumer: %-6s | Month: %s | Units: %7.2f | Total: Rs. %9.2f | Status: %s",
                billId, consumerId, billingMonth, unitsConsumed, totalAmount, paymentStatus);
    }
}
