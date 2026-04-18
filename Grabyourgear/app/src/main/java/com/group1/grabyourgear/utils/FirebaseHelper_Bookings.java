package com.group1.grabyourgear.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Booking;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper_Bookings {

    private static final DatabaseReference BOOKING_REF =
            FirebaseDatabase.getInstance().getReference(FirebaseNodes.BOOKINGS);

    // callback interface
    public interface BookingListCallback {
        void onSuccess(List<Booking> bookingList);
        void onFailure(Exception e);
    }

    // 1. 加载所有 bookings
    public static void loadAllBookings(BookingListCallback callback) {
        BOOKING_REF.get().addOnSuccessListener(snapshot -> {

            List<Booking> list = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                Booking item = child.getValue(Booking.class);
                if (item != null) {
                    list.add(item);
                }
            }

            callback.onSuccess(list);

        }).addOnFailureListener(callback::onFailure);
    }

    // 2. 按设备 ID 查询（用于日期过滤）
    public static void loadBookingsByEquipmentId(String equipmentId, BookingListCallback callback) {
        BOOKING_REF.orderByChild("equipmentId")
                .equalTo(equipmentId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Booking> list = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Booking item = child.getValue(Booking.class);
                        if (item != null) {
                            list.add(item);
                        }
                    }

                    callback.onSuccess(list);

                }).addOnFailureListener(callback::onFailure);
    }

    // 3. 按用户 ID 查询（客户查看自己的订单）
    public static void loadBookingsByUserId(String userId, BookingListCallback callback) {
        BOOKING_REF.orderByChild("userId")
                .equalTo(userId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Booking> list = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Booking item = child.getValue(Booking.class);
                        if (item != null) {
                            list.add(item);
                        }
                    }

                    callback.onSuccess(list);

                }).addOnFailureListener(callback::onFailure);
    }

    // 4. query by supplierID for supplier dashboard
    public static void loadBookingsBySupplierId(String supplierId, BookingListCallback callback) {
        BOOKING_REF.orderByChild("supplierId")
                .equalTo(supplierId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Booking> list = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Booking item = child.getValue(Booking.class);
                        if (item != null) {
                            list.add(item);
                        }
                    }

                    callback.onSuccess(list);

                }).addOnFailureListener(callback::onFailure);
    }
}
