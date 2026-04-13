package com.group1.grabyourgear.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Users;

public class FirebaseHelper_Users {

    private static final DatabaseReference USERS_REF =
            FirebaseDatabase.getInstance().getReference(FirebaseNodes.USERS);

    // load users
    public interface UserCallback {
        void onSuccess(Users user);
        void onFailure(Exception e);
    }

    public static void loadUserInfo(String uid, UserCallback callback) {
        USERS_REF.child(uid).get().addOnSuccessListener(snapshot -> {
            Users user = snapshot.getValue(Users.class);
            callback.onSuccess(user);
        }).addOnFailureListener(callback::onFailure);
    }



    // write users
    public interface RegisterCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void registerUser(Users user, RegisterCallback callback) {
        if (user == null || user.getUid() == null) {
            callback.onFailure(new Exception("User or UID is null"));
            return;
        }

        USERS_REF.child(user.getUid())
                .setValue(user)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
