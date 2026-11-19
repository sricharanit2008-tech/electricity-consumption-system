package com.electricity.model;

/**
 * Model class representing a Tariff Slab for energy rate calculation.
 */
public class TariffSlab {
    private int slabId;
    private String connectionType;
    private int minUnits;
    private int maxUnits;
    private double ratePerUnit;
    private double fixedCharge;
    private double taxPercent;

    public TariffSlab() {}

    public TariffSlab(int slabId, String connectionType, int minUnits, int maxUnits, 
                      double ratePerUnit, double fixedCharge, double taxPercent) {
        this.slabId = slabId;
        this.connectionType = connectionType;
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
        this.ratePerUnit = ratePerUnit;
        this.fixedCharge = fixedCharge;
        this.taxPercent = taxPercent;
    }

    public int getSlabId() { return slabId; }
    public void setSlabId(int slabId) { this.slabId = slabId; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public int getMinUnits() { return minUnits; }
    public void setMinUnits(int minUnits) { this.minUnits = minUnits; }

    public int getMaxUnits() { return maxUnits; }
    public void setMaxUnits(int maxUnits) { this.maxUnits = maxUnits; }

    public double getRatePerUnit() { return ratePerUnit; }
    public void setRatePerUnit(double ratePerUnit) { this.ratePerUnit = ratePerUnit; }

    public double getFixedCharge() { return fixedCharge; }
    public void setFixedCharge(double fixedCharge) { this.fixedCharge = fixedCharge; }

    public double getTaxPercent() { return taxPercent; }
    public void setTaxPercent(double taxPercent) { this.taxPercent = taxPercent; }

    @Override
    public String toString() {
        return String.format("[%s] Units: %4d - %-6s | Rate: Rs. %5.2f/unit | Fixed: Rs. %6.2f | Tax: %.1f%%",
                connectionType, minUnits, (maxUnits >= 999999 ? "Above" : String.valueOf(maxUnits)), 
                ratePerUnit, fixedCharge, taxPercent);
    }
}
