package com.electricity.dao;

import com.electricity.model.Bill;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Data Access Object for Bills.
 * Functional Requirement 4 & 5: Calculate and generate electricity bills.
 */
public class BillDAO {
    private static final Map<String, Bill> billStore = new LinkedHashMap<>();

    static {
        initDefaultBills();
    }

    private static void initDefaultBills() {
        addSample(new Bill("BILL-202401-C101", 1, "C101", "2024-01", 140.0, 558.00, 75.00, 31.65, 664.65, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202402-C101", 2, "C101", "2024-02", 165.0, 688.00, 75.00, 38.15, 801.15, LocalDate.of(2024, 3, 5), LocalDate.of(2024, 3, 20), "Paid"));
        addSample(new Bill("BILL-202403-C101", 3, "C101", "2024-03", 220.0, 974.00, 75.00, 52.45, 1101.45, LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 20), "Paid"));
        addSample(new Bill("BILL-202404-C101", 4, "C101", "2024-04", 270.0, 1234.00, 75.00, 65.45, 1374.45, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));

        addSample(new Bill("BILL-202401-C103", 9, "C103", "2024-01", 280.0, 1286.00, 75.00, 68.05, 1429.05, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202404-C103", 12, "C103", "2024-04", 650.0, 4360.00, 150.00, 225.50, 4735.50, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));

        addSample(new Bill("BILL-202401-C201", 21, "C201", "2024-01", 850.0, 9100.00, 500.00, 864.00, 10464.00, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202404-C201", 24, "C201", "2024-04", 1260.0, 14635.00, 500.00, 1362.15, 16497.15, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));

        addSample(new Bill("BILL-202401-C301", 33, "C301", "2024-01", 4200.0, 49000.00, 1500.00, 6060.00, 56560.00, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202404-C301", 36, "C301", "2024-04", 5850.0, 69625.00, 1500.00, 8535.00, 79660.00, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));

        addSample(new Bill("BILL-202401-C401", 41, "C401", "2024-01", 350.0, 525.00, 30.00, 11.10, 566.10, LocalDate.of(2024, 2, 5), LocalDate.of(2024, 2, 20), "Paid"));
        addSample(new Bill("BILL-202404-C401", 44, "C401", "2024-04", 660.0, 1198.00, 50.00, 24.96, 1272.96, LocalDate.of(2024, 5, 5), LocalDate.of(2024, 5, 20), "Unpaid"));
    }

    private static void addSample(Bill b) {
        billStore.put(b.getBillId(), b);
    }

    public boolean saveBill(Bill bill) {
        billStore.put(bill.getBillId(), bill);

        if (DBConnection.isDriverAvailable()) {
            String sql = "INSERT INTO bills (bill_id, reading_id, consumer_id, billing_month, units_consumed, " +
                         "energy_charges, fixed_charges, tax_amount, total_amount, bill_date, due_date, payment_status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, bill.getBillId());
                stmt.setInt(2, bill.getReadingId());
                stmt.setString(3, bill.getConsumerId());
                stmt.setString(4, bill.getBillingMonth());
                stmt.setDouble(5, bill.getUnitsConsumed());
                stmt.setDouble(6, bill.getEnergyCharges());
                stmt.setDouble(7, bill.getFixedCharges());
                stmt.setDouble(8, bill.getTaxAmount());
                stmt.setDouble(9, bill.getTotalAmount());
                stmt.setDate(10, java.sql.Date.valueOf(bill.getBillDate()));
                stmt.setDate(11, java.sql.Date.valueOf(bill.getDueDate()));
                stmt.setString(12, bill.getPaymentStatus());
                stmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.err.println("[BillDAO] DB Insert Error: " + e.getMessage());
            }
        }
        return true;
    }

    public List<Bill> getAllBills() {
        if (DBConnection.isDriverAvailable()) {
            List<Bill> list = new ArrayList<>();
            String sql = "SELECT * FROM bills ORDER BY billing_month DESC, consumer_id";
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                if (!list.isEmpty()) return list;
            } catch (SQLException e) {
                System.err.println("[BillDAO] DB Query Error: " + e.getMessage());
            }
        }
        return new ArrayList<>(billStore.values());
    }

    public List<Bill> getBillsByConsumer(String consumerId) {
        List<Bill> list = new ArrayList<>();
        for (Bill b : getAllBills()) {
            if (b.getConsumerId().equalsIgnoreCase(consumerId)) {
                list.add(b);
            }
        }
        return list;
    }

    public Bill getBillById(String billId) {
        for (Bill b : getAllBills()) {
            if (b.getBillId().equalsIgnoreCase(billId)) {
                return b;
            }
        }
        return null;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill b = new Bill();
        b.setBillId(rs.getString("bill_id"));
        b.setReadingId(rs.getInt("reading_id"));
        b.setConsumerId(rs.getString("consumer_id"));
        b.setBillingMonth(rs.getString("billing_month"));
        b.setUnitsConsumed(rs.getDouble("units_consumed"));
        b.setEnergyCharges(rs.getDouble("energy_charges"));
        b.setFixedCharges(rs.getDouble("fixed_charges"));
        b.setTaxAmount(rs.getDouble("tax_amount"));
        b.setTotalAmount(rs.getDouble("total_amount"));
        Date bDate = rs.getDate("bill_date");
        b.setBillDate(bDate != null ? bDate.toLocalDate() : LocalDate.now());
        Date dDate = rs.getDate("due_date");
        b.setDueDate(dDate != null ? dDate.toLocalDate() : LocalDate.now().plusDays(15));
        b.setPaymentStatus(rs.getString("payment_status"));
        return b;
    }
}
