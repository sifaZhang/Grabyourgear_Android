package com.group1.grabyourgear.utils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Category;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelper_Categories {
    private static final DatabaseReference CATEGORY_REF =
            FirebaseDatabase.getInstance().getReference(FirebaseNodes.CATEGORIES);

    public interface CategoryListCallback {
        void onSuccess(List<Category> list);
        void onFailure(Exception e);
    }

    public static void loadAllCategories(CategoryListCallback callback) {
        CATEGORY_REF.get().addOnSuccessListener(snapshot -> {

            List<Category> list = new ArrayList<>();

            for (DataSnapshot child : snapshot.getChildren()) {
                Category category = null;
                Object value = child.getValue();

                if (value instanceof String) {
                    // Handle case where category is just a String name in DB
                    category = new Category(child.getKey(), (String) value);
                } else if (value != null) {
                    // Handle case where category is an object in DB
                    category = child.getValue(Category.class);
                    if (category != null) {
                        category.setCtId(child.getKey());
                    }
                }

                if (category != null) {
                    list.add(category);
                }
            }

            callback.onSuccess(list);

        }).addOnFailureListener(callback::onFailure);
    }
}
