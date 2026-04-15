package com.group1.grabyourgear.utils;

import com.group1.grabyourgear.models.Users;

public class UserManager {

    private static UserManager instance;
    private Users currentUser;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setUser(Users user) {
        this.currentUser = user;
    }

    public Users getUser() {
        return currentUser;
    }

    public void clear() {
        currentUser = null;
    }

    public boolean isApproved() {
        if (currentUser != null)
            return currentUser.isApproved();

        return false;
    }
}

