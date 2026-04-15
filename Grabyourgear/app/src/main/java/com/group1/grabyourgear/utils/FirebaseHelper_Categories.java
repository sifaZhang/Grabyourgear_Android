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

                // 读取 Category 对象
                Category category = child.getValue(Category.class);

                if (category != null) {
                    category.setCtId(child.getKey());
                    list.add(category);
                }
            }

            callback.onSuccess(list);

        }).addOnFailureListener(callback::onFailure);
    }
}
