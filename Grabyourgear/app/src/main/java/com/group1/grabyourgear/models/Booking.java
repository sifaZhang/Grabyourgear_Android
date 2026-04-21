package com.group1.grabyourgear.models;

public class Booking {

    private String id;
    private String equipmentId;
    private String userId;
    private String supplierId;
    private long startDate;
    private long endDate;
    private double totalPrice;
    private String status;
    private long timestamp;

    // Firebase required empty constructor
    public Booking() {}

    // Full constructor (id optional depending on your usage)
    public Booking(String id, String equipmentId, String userId, String supplierId,
                   long startDate, long endDate, double totalPrice,
                   String status, long timestamp) {

        this.id = id;
        this.equipmentId = equipmentId;
        this.userId = userId;
        this.supplierId = supplierId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Getter & Setter for id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Other getters & setters
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

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
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
