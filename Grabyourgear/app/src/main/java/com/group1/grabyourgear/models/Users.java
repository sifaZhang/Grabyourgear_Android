package com.group1.grabyourgear.models;

public class Users {
    public String uid;
    public String name;
    public String username;
    public String role;
    public String avatar;
    public boolean isApproved;
    public String phone;
    public String address;

    public String email;

    public Users() {}

    public Users(String uid, String name, String username, String email,
                String phone, String address, String role, String avatar, boolean isApproved) {
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

    public String getUid(){
        return uid;
    }
}
