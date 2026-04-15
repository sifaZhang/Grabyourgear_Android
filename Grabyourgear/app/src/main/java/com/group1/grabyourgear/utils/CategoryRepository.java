package com.group1.grabyourgear.utils;

import com.group1.grabyourgear.models.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private static CategoryRepository instance;
    private List<Category> cachedCategories = new ArrayList<>();

    private CategoryRepository() {}

    public static CategoryRepository getInstance() {
        if (instance == null) {
            instance = new CategoryRepository();
        }
        return instance;
    }

    public List<Category> getCachedCategories() {
        return cachedCategories;
    }

    public void setCachedCategories(List<Category> list) {
        cachedCategories = list;
    }

    public void clearCachedCategories() {
        cachedCategories.clear();
    }

    public String getCategoryName(String categoryId) {
        for (Category category : cachedCategories) {
            if (category.getCtId().equals(categoryId)) {
                return category.getName();
            }
        }

        return "Unknown Category";
    }
}

