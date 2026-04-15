package com.group1.grabyourgear.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Equipment;

import java.util.ArrayList;
import java.util.List;

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
}
