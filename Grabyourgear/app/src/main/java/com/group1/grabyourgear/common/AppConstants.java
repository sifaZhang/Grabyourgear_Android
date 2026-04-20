package com.group1.grabyourgear.common;

public class AppConstants {

    public static String BK_COLOR = "#E7EFE6";

    public static String DEFAULT_DATE = "Select Date Range";

    public static class IntenParamer {
        public static final String SEARCH_STRING = "searchString";
        public static final String CATEGORY = "GrabYourGearPrefs";
        public static final String EQUIPMENT_ID = "equipmentId";
        public static final String EQUIPMENT_NAME = "equipmentName";
    }

    // SharedPreferences
    public static class PrefUser {
        public static final String PREF_NAME = "GrabYourGearPrefs";
        public static final String KEY_UID = "uid";
        public static final String KEY_LOGGED_IN = "isLoggedIn";
    }


    //roles
    public static class Role {
        public static final String CUSTOMER = "customer";
        public static final String SUPPLIER = "supplier";
        public static final String ADMIN = "admin";

        public static final String SERVICE = "service";
    }

    //category
    public static class CurrentCategory {
        public static final String ALL = "All";
        public static final String VEHICLE = "Vehicle";
        public static final String CONSTRUCTION = "Construction";
        public static final String ELECTRONIC = "Electronic";
        public static final String OFFICE = "Office";
    }
}
