package com.electricity.model;

import java.time.LocalDate;

/**
 * Model class representing a monthly meter reading.
 */
public class MeterReading {
    private int readingId;
    private String consumerId;
    private String billingMonth; // Format 'YYYY-MM'
    private LocalDate readingDate;
    private double previousReading;
    private double currentReading;
    private double unitsConsumed;

    public MeterReading() {}

    public MeterReading(int readingId, String consumerId, String billingMonth, LocalDate readingDate,
                        double previousReading, double currentReading) {
        this.readingId = readingId;
        this.consumerId = consumerId;
        this.billingMonth = billingMonth;
        this.readingDate = readingDate;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.unitsConsumed = calculateUnitsConsumed();
    }

    public MeterReading(int readingId, String consumerId, String billingMonth, LocalDate readingDate,
                        double previousReading, double currentReading, double unitsConsumed) {
        this.readingId = readingId;
        this.consumerId = consumerId;
        this.billingMonth = billingMonth;
        this.readingDate = readingDate;
        this.previousReading = previousReading;
        this.currentReading = currentReading;
        this.unitsConsumed = unitsConsumed;
    }

    /**
     * Requirement 3: Calculate electricity consumption by subtracting previous reading from current reading.
     */
    public double calculateUnitsConsumed() {
        if (currentReading < previousReading) {
            throw new IllegalArgumentException("Current reading cannot be less than previous reading.");
        }
        return Math.round((currentReading - previousReading) * 100.0) / 100.0;
    }

    // Getters and Setters
    public int getReadingId() { return readingId; }
    public void setReadingId(int readingId) { this.readingId = readingId; }

    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public String getBillingMonth() { return billingMonth; }
    public void setBillingMonth(String billingMonth) { this.billingMonth = billingMonth; }

    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public double getPreviousReading() { return previousReading; }
    public void setPreviousReading(double previousReading) { 
        this.previousReading = previousReading;
        this.unitsConsumed = calculateUnitsConsumed();
    }

    public double getCurrentReading() { return currentReading; }
    public void setCurrentReading(double currentReading) { 
        this.currentReading = currentReading;
        this.unitsConsumed = calculateUnitsConsumed();
    }

    public double getUnitsConsumed() { return unitsConsumed; }
    public void setUnitsConsumed(double unitsConsumed) { this.unitsConsumed = unitsConsumed; }

    @Override
    public String toString() {
        return String.format("Reading #%d | Consumer: %-6s | Month: %s | Prev: %8.2f | Curr: %8.2f | Units: %8.2f",
                readingId, consumerId, billingMonth, previousReading, currentReading, unitsConsumed);
    }
}
