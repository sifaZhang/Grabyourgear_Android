package com.group1.grabyourgear.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Booking;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper_Bookings {

    ////// Read booking from firebase
    private static final DatabaseReference BOOKING_REF =
            FirebaseDatabase.getInstance().getReference(FirebaseNodes.BOOKINGS);

    // callback interface
    public interface BookingListCallback {
        void onSuccess(List<Booking> bookingList);
        void onFailure(Exception e);
    }

    // 1. Load all bookings
    public static void loadAllBookings(BookingListCallback callback) {
        BOOKING_REF.get().addOnSuccessListener(snapshot -> {

            List<Booking> list = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                Booking item = child.getValue(Booking.class);
                if (item != null) {
                    item.setId(child.getKey());
                    list.add(item);
                }
            }

            callback.onSuccess(list);

        }).addOnFailureListener(callback::onFailure);
    }

    // 2. Load bookings by equipment ID
    public static void loadBookingsByEquipmentId(String equipmentId, BookingListCallback callback) {
        BOOKING_REF.get().addOnSuccessListener(snapshot -> {

            List<Booking> list = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                Booking item = child.getValue(Booking.class);

                if (item != null && equipmentId.equals(item.getEquipmentId())) {
                    item.setId(child.getKey());
                    list.add(item);
                }
            }

            callback.onSuccess(list);

        }).addOnFailureListener(callback::onFailure);
    }

    // 3. Load bookings by user ID
    public static void loadBookingsByUserId(String userId, BookingListCallback callback) {
        BOOKING_REF.get().addOnSuccessListener(snapshot -> {

            List<Booking> list = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                Booking item = child.getValue(Booking.class);

                if (item != null && userId.equals(item.getUserId())) {
                    item.setId(child.getKey());   // <-- IMPORTANT
                    list.add(item);
                }
            }

            callback.onSuccess(list);

        }).addOnFailureListener(callback::onFailure);
    }


    ////// Write data to firebase
    public interface BookingCreateCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void createBooking(Booking booking, BookingCreateCallback callback) {

        DatabaseReference ref = BOOKING_REF.push(); // auto ID
        booking.setId(ref.getKey());

        ref.setValue(booking)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }


    ////// Update booking status
    public interface OnBookingUpdateListener {
        void onSuccess();
        void onFailure(String error);
    }

    // Update Booking status
    public static void updateBookingStatus(String bookingId, String newStatus,
                                           OnBookingUpdateListener listener) {
        BOOKING_REF.child(bookingId).child(FirebaseNodes.BookingsFields.STATUS)
                .setValue(newStatus)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }
}
