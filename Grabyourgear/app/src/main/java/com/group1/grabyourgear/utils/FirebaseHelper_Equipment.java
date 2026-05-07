package com.group1.grabyourgear.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseHelper_Equipment {
    private static final DatabaseReference EQUIPMENT_REF =
            FirebaseDatabase.getInstance().getReference(FirebaseNodes.EQUIPMENT);

    // callback interface
    public interface EquipmentListCallback {
        void onSuccess(List<Equipment> equipmentList);
        void onFailure(Exception e);
    }

    // load all equipment
    public static void loadAllEquipment(EquipmentListCallback callback) {
        EQUIPMENT_REF.get().addOnSuccessListener(snapshot -> {

            List<Equipment> list = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                Equipment item = child.getValue(Equipment.class);
                if (item != null) {
                    item.setId(child.getKey()); // eq001, eq002...
                    list.add(item);
                }
            }

            callback.onSuccess(list);

        }).addOnFailureListener(callback::onFailure);
    }


    //lock equipment
    public static void lockEquipment(String equipmentId, Runnable onSuccess, OnFailureListener onFailure) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseNodes.EQUIPMENT)
                .child(equipmentId)
                .child(FirebaseNodes.EquipmentFields.IS_LOCKED);

        ref.setValue(true)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(onFailure);
    }

    //lock equipment
    public static void unLockEquipment(String equipmentId, Runnable onSuccess, OnFailureListener onFailure) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference(FirebaseNodes.EQUIPMENT)
                .child(equipmentId)
                .child(FirebaseNodes.EquipmentFields.IS_LOCKED);

        ref.setValue(false)
                .addOnSuccessListener(aVoid -> {
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    if (onFailure != null) onFailure.onFailure(e);
                });
    }

    //update rating and count
    public interface OnEquipmentUpdateListener {
        void onSuccess();
        void onFailure(String error);
    }

    public static void updateRatingAndCount(String equipmentId, double rating, int rateCount,
                                            OnEquipmentUpdateListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("rating", rating);
        updates.put("rateCount", rateCount);

        EQUIPMENT_REF.child(equipmentId)
                .updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }
}
