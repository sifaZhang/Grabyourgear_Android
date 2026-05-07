package com.group1.grabyourgear.supplier;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.Adapter_SupplierEquipmentView;
import com.group1.grabyourgear.models.Category;
import com.group1.grabyourgear.utils.CategoryRepository;
import com.group1.grabyourgear.utils.FirebaseHelper_Categories;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.models.Equipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SupplierMyEquipmentActivity extends BaseActivity {
    RecyclerView recyclerView;
    List<Equipment> allEquipmentList, filteredEquipmentList;
    Adapter_SupplierEquipmentView adapter;
    Spinner spCategory, spStatus;
    EditText etLocation, etKeyword;
    Button btnSearch, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_my_equipment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("My Equipment");

        spCategory = findViewById(R.id.spFilterCategorySupplier);
        spStatus = findViewById(R.id.spFilterStatusSupplier);
        etLocation = findViewById(R.id.etFilterLocationSupplier);
        etKeyword = findViewById(R.id.etFilterKeywordSupplier);
        btnClear = findViewById(R.id.btnClearMyEquipSupplier);
        btnSearch = findViewById(R.id.btnSearchMyEquipSupplier);
        recyclerView = findViewById(R.id.rvMyProductSupplier);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        allEquipmentList = new ArrayList<>();
        filteredEquipmentList = new ArrayList<>();

        setupFilters();

        adapter = new Adapter_SupplierEquipmentView(this, filteredEquipmentList);
        recyclerView.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applySearch();
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spCategory.setSelection(0);
                spStatus.setSelection(0);
                etKeyword.setText("");
                etLocation.setText("");

                filteredEquipmentList.clear();
                filteredEquipmentList.addAll(allEquipmentList);
                adapter.notifyDataSetChanged();
            }
        });

        loadCategoriesThenEquipment();
    }

    private void setupFilters() {
        List<String> categories = Arrays.asList(
                AppConstants.CurrentCategory.ALL,
                AppConstants.CurrentCategory.VEHICLE,
                AppConstants.CurrentCategory.CONSTRUCTION,
                AppConstants.CurrentCategory.ELECTRONIC,
                AppConstants.CurrentCategory.OFFICE
        );

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        List<String> statuses = Arrays.asList(
                "All",
                "available",
                "unavailable",
                "rented"
        );

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statuses
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);
    }

    private void loadCategoriesThenEquipment() {
        FirebaseHelper_Categories.loadAllCategories(new FirebaseHelper_Categories.CategoryListCallback() {
            @Override
            public void onSuccess(List<Category> list) {
                CategoryRepository.getInstance().clearCachedCategories();
                CategoryRepository.getInstance().setCachedCategories(list);
                loadMyEquipment();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(
                        SupplierMyEquipmentActivity.this,
                        "Failed to load categories.",
                        Toast.LENGTH_SHORT
                ).show();

                loadMyEquipment();
            }
        });
    }
    private void loadMyEquipment() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String supplierId = firebaseUser.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("equipment");

        ref.orderByChild("supplierId")
                .equalTo(supplierId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allEquipmentList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Equipment equipment = ds.getValue(Equipment.class);

                            if(equipment != null) {
                                equipment.setId(ds.getKey());
                                allEquipmentList.add(equipment);
                            }
                        }

                        filteredEquipmentList.clear();
                        filteredEquipmentList.addAll(allEquipmentList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SupplierMyEquipmentActivity.this,
                                "Failed to load products.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void applySearch() {
        filteredEquipmentList.clear();

        String selectedCategory = spCategory.getSelectedItem().toString();
        String selectedStatus = spStatus.getSelectedItem().toString();
        String locationInput = etLocation.getText().toString().trim().toLowerCase();
        String keywordInput = etKeyword.getText().toString().trim().toLowerCase();

        for (Equipment eq : allEquipmentList) {
            String categoryName = CategoryRepository.getInstance().getCategoryName(eq.getCategoryId());

            boolean matchesCategory = selectedCategory.equals("All")
                    || categoryName.equalsIgnoreCase(selectedCategory);

            boolean matchesStatus = selectedStatus.equals("All")
                    || (eq.getStatus() != null && eq.getStatus().equalsIgnoreCase(selectedStatus));

            boolean matchesLocation = locationInput.isEmpty()
                    || eq.getLocation().toLowerCase().contains(locationInput);

            boolean matchesKeyword = keywordInput.isEmpty()
                    || (eq.getName() != null && eq.getName().toLowerCase().contains(keywordInput)
                    || (eq.getDescription() != null && eq.getDescription().toLowerCase().contains(keywordInput)));

            if(matchesCategory && matchesStatus && matchesLocation && matchesKeyword) {
                filteredEquipmentList.add(eq);
            }
        }

        adapter.notifyDataSetChanged();
    }
}