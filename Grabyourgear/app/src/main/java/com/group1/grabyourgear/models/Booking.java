package com.group1.grabyourgear.models;

public class Booking {

    private String equipmentId;
    private String userId;
    private String supplierId;
    private String startDate;
    private String endDate;
    private long totalPrice;
    private String status;
    private long timestamp;

    // Firebase
    public Booking() {}

    //  全参构造函数
    public Booking(String equipmentId, String userId, String supplierId,
                   String startDate, String endDate, long totalPrice,
                   String status, long timestamp) {

        this.equipmentId = equipmentId;
        this.userId = userId;
        this.supplierId = supplierId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getter & Setter
    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
