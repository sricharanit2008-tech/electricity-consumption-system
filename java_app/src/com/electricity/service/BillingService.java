package com.electricity.service;

import com.electricity.dao.BillDAO;
import com.electricity.dao.ConsumerDAO;
import com.electricity.dao.MeterReadingDAO;
import com.electricity.model.Bill;
import com.electricity.model.Consumer;
import com.electricity.model.MeterReading;

import java.time.LocalDate;

/**
 * Service to calculate electricity bills based on predefined tariff rates and applicable charges.
 * Functional Requirement 4: Calculate electricity bills using predefined tariff rates and applicable charges.
 */
public class BillingService {
    private final ConsumerDAO consumerDAO;
    private final MeterReadingDAO meterReadingDAO;
    private final BillDAO billDAO;

    public BillingService() {
        this.consumerDAO = new ConsumerDAO();
        this.meterReadingDAO = new MeterReadingDAO();
        this.billDAO = new BillDAO();
    }

    public BillingService(ConsumerDAO cDAO, MeterReadingDAO mrDAO, BillDAO bDAO) {
        this.consumerDAO = cDAO;
        this.meterReadingDAO = mrDAO;
        this.billDAO = bDAO;
    }

    /**
     * Calculates bill for a specific meter reading.
     */
    public Bill generateBillForReading(int readingId) {
        MeterReading reading = null;
        for (MeterReading r : meterReadingDAO.getAllReadings()) {
            if (r.getReadingId() == readingId) {
                reading = r;
                break;
            }
        }

        if (reading == null) {
            throw new IllegalArgumentException("Meter Reading ID " + readingId + " not found.");
        }

        Consumer consumer = consumerDAO.getConsumerById(reading.getConsumerId());
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer ID " + reading.getConsumerId() + " not found.");
        }

        double units = reading.getUnitsConsumed();
        String connType = consumer.getConnectionType();

        double energyCharges = 0.0;
        double fixedCharges = 0.0;
        double taxRate = 0.05; // default 5%

        // Calculation logic based on Connection Type and Progressive Slabs
        switch (connType.trim()) {
            case "Domestic":
                // Tier 1: 0 - 100 units @ 3.50
                // Tier 2: 101 - 300 units @ 5.20
                // Tier 3: 301 - 500 units @ 7.50
                // Tier 4: Above 500 units @ 9.80
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

            case "Commercial":
                // Tier 1: 0 - 200 units @ 8.50
                // Tier 2: 201 - 750 units @ 11.00
                // Tier 3: Above 750 units @ 13.50
                taxRate = 0.09; // 9% GST
                if (units <= 200) {
                    energyCharges = units * 8.50;
                    fixedCharges = 200.00;
                } else if (units <= 750) {
                    energyCharges = (200 * 8.50) + ((units - 200) * 11.00);
                    fixedCharges = 350.00;
                } else {
                    energyCharges = (200 * 8.50) + (550 * 11.00) + ((units - 750) * 13.50);
                    fixedCharges = 500.00;
                }
                break;

            case "Industrial":
                // Tier 1: 0 - 1000 units @ 9.00
                // Tier 2: Above 1000 units @ 12.50
                taxRate = 0.12; // 12% Industrial duty & cess
                if (units <= 1000) {
                    energyCharges = units * 9.00;
                    fixedCharges = 800.00;
                } else {
                    energyCharges = (1000 * 9.00) + ((units - 1000) * 12.50);
                    fixedCharges = 1500.00;
                }
                break;

            case "Agricultural":
                // Subsidized: 0 - 500 units @ 1.50, above @ 2.80
                taxRate = 0.02; // 2% Agricultural cess
                if (units <= 500) {
                    energyCharges = units * 1.50;
                    fixedCharges = 30.00;
                } else {
                    energyCharges = (500 * 1.50) + ((units - 500) * 2.80);
                    fixedCharges = 50.00;
                }
                break;

            default:
                // Flat fallback
                energyCharges = units * 6.00;
                fixedCharges = 100.00;
                taxRate = 0.05;
        }

        energyCharges = Math.round(energyCharges * 100.0) / 100.0;
        double taxAmount = Math.round((energyCharges + fixedCharges) * taxRate * 100.0) / 100.0;
        double totalAmount = Math.round((energyCharges + fixedCharges + taxAmount) * 100.0) / 100.0;

        String billId = String.format("BILL-%s-%s", 
                reading.getBillingMonth().replace("-", ""), consumer.getConsumerId());

        LocalDate billDate = LocalDate.now();
        LocalDate dueDate = billDate.plusDays(15);

        Bill bill = new Bill(billId, reading.getReadingId(), consumer.getConsumerId(),
                reading.getBillingMonth(), units, energyCharges, fixedCharges, taxAmount, totalAmount,
                billDate, dueDate, "Unpaid");

        billDAO.saveBill(bill);
        return bill;
    }
}
