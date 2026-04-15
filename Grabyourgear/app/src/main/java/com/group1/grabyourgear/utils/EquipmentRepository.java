package com.group1.grabyourgear.utils;

import com.group1.grabyourgear.models.Equipment;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {

    private static EquipmentRepository instance;
    private List<Equipment> cachedEquipment = new ArrayList<>();

    private EquipmentRepository() {}

    public static EquipmentRepository getInstance() {
        if (instance == null) {
            instance = new EquipmentRepository();
        }
        return instance;
    }

    public List<Equipment> getCachedEquipment() {
        return cachedEquipment;
    }

    public void setCachedEquipment(List<Equipment> list) {
        cachedEquipment = list;
    }

    public void clearCachedEquipment() {
        cachedEquipment.clear();
    }
}

