package com.menumitra.apiRequest;

public class UdhariLedgerApiRequest {
    private int userId;
    private String customerName;
    private String customerMobile;
    private String customerAddress;
    private int orderId;
    private double billAmount;
    private String estimatedSettlementPeriod;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public void setCustomerMobile(String customerMobile) {
        this.customerMobile = customerMobile;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(double billAmount) {
        this.billAmount = billAmount;
    }

    public String getEstimatedSettlementPeriod() {
        return estimatedSettlementPeriod;
    }

    public void setEstimatedSettlementPeriod(String estimatedSettlementPeriod) {
        this.estimatedSettlementPeriod = estimatedSettlementPeriod;
    }
}