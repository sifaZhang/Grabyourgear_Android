package com.group1.grabyourgear.models;

public class Equipment {

    private String id;
    private String name;
    private String categoryId;
    private String supplierId;
    private double pricePerDay;
    private double discount;
    private String description;
    private String imageUrl;
    private String location;
    private double rating;
    private int rateCount;
    private String status;
    private boolean isLocked;

    // Empty constructor (required for Firestore)
    public Equipment() {}

    // Full constructor
    public Equipment(String id, String name, String categoryId, String supplierId,
                     double pricePerDay, double discount, String description,
                     String imageUrl, String location, double rating, String status,
                     boolean isLocked, int rateCount) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.pricePerDay = pricePerDay;
        this.discount = discount;
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
        this.rating = rating;
        this.rateCount = rateCount;
        this.status = status;
        this.isLocked = isLocked;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getRateCount() { return rateCount; }
    public void setRateCount(int rateCount) { this.rateCount = rateCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
}

