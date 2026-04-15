package com.group1.grabyourgear.models;

public class Category {

    private String ctId;
    private String name;

    public Category() {}

    public Category(String ctId, String name) {
        this.ctId = ctId;
        this.name = name;
    }

    public String getCtId() {
        return ctId;
    }

    public void setCtId(String ctId) {
        this.ctId = ctId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

