package com.group1.grabyourgear.common;

public class FirebaseNodes {

    public static final double FEATURED_DISCOUNT = 20;
    public static final String USERS = "users";
    public static class UserFields {
        public static final String UID = "uid";
        public static final String FULLNAME = "name";
        public static final String USERNAME = "username";
        public static final String EMAIL = "email";
        public static final String PHONE = "phone";
        public static final String ADDRESS = "address";
        public static final String ROLE = "role";
        public static final String AVATAR = "avatar";
        public static final String IS_APPROVED = "isApproved";
    }


    public static final String CATEGORIES = "categories";
    public static class CATEGORIESFields {
        public static final String ID = "ctId";
        public static final String NAME = "name";
    }

    public static final String EQUIPMENT = "equipment";
    public static class EquipmentFields {
        public static final String ID = "eqId";
        public static final String NAME = "name";
        public static final String CATEGORY_ID = "categoryId";
        public static final String SUPPLIER_ID = "supplierId";
        public static final String PRICE_PER_DAY = "pricePerDay";
        public static final String DISCOUNT = "discount";
        public static final String DESCRIPTION = "description";
        public static final String IMAGE_URL = "imageUrl";
        public static final String LOCATION = "location";
        public static final String RATING = "rating";
        public static final String STATUS = "status";
    }

    public static class EquipmentStatus {
        public static final String AVAILABLE = "available";
        public static final String UNAVAILABLE = "unavailable";
        public static final String MAINTENANCE = "maintenance";
        public static final String RENTED = "rented";
    }


    public static final String BOOKINGS = "bookings";



    public static final String FAQ = "faq";

    public static final String INQUIRIES = "inquiries";

    public static final String FEEDBACK = "feedback";
}
