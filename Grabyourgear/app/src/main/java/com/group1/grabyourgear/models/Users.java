package com.group1.grabyourgear.models;

import com.google.firebase.database.PropertyName;

public class Users {

    private String uid;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String role;
    private String avatar;
    @PropertyName("isApproved")
    private boolean isApproved;

    // Empty constructor (required for Firestore)
    public Users() {}

    // Full constructor
    public Users(String uid, String name, String username, String email,
                 String phone, String address, String role,
                 String avatar, boolean isApproved) {

        this.uid = uid;
        this.name = name;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.avatar = avatar;
        this.isApproved = isApproved;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    @PropertyName("isApproved")
    public boolean isApproved() { return isApproved; }
    @PropertyName("isApproved")
    public void setApproved(boolean approved) { isApproved = approved; }
}

