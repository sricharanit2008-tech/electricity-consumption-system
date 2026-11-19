package com.electricity.model;

import java.time.LocalDate;

/**
 * Model class representing a Consumer in the Electricity Distribution System.
 */
public class Consumer {
    private String consumerId;
    private String name;
    private String address;
    private String phone;
    private String connectionType; // Domestic, Commercial, Industrial, Agricultural
    private String meterNumber;
    private LocalDate connectionDate;
    private String status;         // Active, Suspended, Disconnected

    public Consumer() {
        this.status = "Active";
        this.connectionDate = LocalDate.now();
    }

    public Consumer(String consumerId, String name, String address, String phone, 
                    String connectionType, String meterNumber, LocalDate connectionDate, String status) {
        this.consumerId = consumerId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.connectionType = connectionType;
        this.meterNumber = meterNumber;
        this.connectionDate = connectionDate;
        this.status = status;
    }

    // Getters and Setters
    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getConnectionType() { return connectionType; }
    public void setConnectionType(String connectionType) { this.connectionType = connectionType; }

    public String getMeterNumber() { return meterNumber; }
    public void setMeterNumber(String meterNumber) { this.meterNumber = meterNumber; }

    public LocalDate getConnectionDate() { return connectionDate; }
    public void setConnectionDate(LocalDate connectionDate) { this.connectionDate = connectionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[%s] %-20s | Type: %-12s | Meter: %-12s | Phone: %s | Status: %s",
                consumerId, name, connectionType, meterNumber, phone, status);
    }
}
