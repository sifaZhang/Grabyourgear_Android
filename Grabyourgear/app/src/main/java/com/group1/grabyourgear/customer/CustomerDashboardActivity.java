package com.group1.grabyourgear.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Category;
import com.group1.grabyourgear.models.Equipment;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.CategoryRepository;
import com.group1.grabyourgear.utils.EquipmentRepository;
import com.group1.grabyourgear.utils.EquipmentView_Adapter;
import com.group1.grabyourgear.utils.FirebaseHelper_Categories;
import com.group1.grabyourgear.utils.FirebaseHelper_Equipment;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboardActivity extends BaseActivity {
    RecyclerView recyclerView;

    LinearLayout lyOffice, lyElectronics, lyConstruction, lyVehicles;
    EditText etSearch;
    TextView tvOffice, tvElectronics, tvConstruction, tvVehicles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("GrabYourGear");

        lyOffice = findViewById(R.id.lyOffice);
        lyElectronics = findViewById(R.id.lyElectronics);
        lyConstruction = findViewById(R.id.lyConstruction);
        lyVehicles = findViewById(R.id.lyVehicles);
        etSearch = findViewById(R.id.etSearch);
        recyclerView = findViewById(R.id.recyclerDeals);
        tvOffice = findViewById(R.id.tvOffice);
        tvElectronics = findViewById(R.id.tvElectronics);
        tvConstruction = findViewById(R.id.tvConstruction);
        tvVehicles = findViewById(R.id.tvVehicles);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //cache all categories
        FirebaseHelper_Categories.loadAllCategories(new FirebaseHelper_Categories.CategoryListCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                CategoryRepository.getInstance().clearCachedCategories();
                CategoryRepository.getInstance().setCachedCategories(categories);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        //cache all equipment
        FirebaseHelper_Equipment.loadAllEquipment(new FirebaseHelper_Equipment.EquipmentListCallback() {
            @Override
            public void onSuccess(List<Equipment> equipmentList) {
                EquipmentRepository.getInstance().clearCachedEquipment();
                EquipmentRepository.getInstance().setCachedEquipment(equipmentList);

                //filter available and discount > 20
                List<Equipment> equipments = new ArrayList<>();
                for (Equipment d : equipmentList) {
                    if (d.getDiscount() >= FirebaseNodes.FEATURED_DISCOUNT && d.getStatus().equals(FirebaseNodes.EquipmentStatus.AVAILABLE)) {
                        equipments.add(d);
                    }
                }

                EquipmentView_Adapter adapter = new EquipmentView_Adapter(CustomerDashboardActivity.this, equipments);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(CustomerDashboardActivity.this, "Failed," + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = etSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {

                    Intent intent = new Intent(CustomerDashboardActivity.this, CustomerEquipmentListActivity.class);
                    intent.putExtra("searchString", keyword);
                    startActivity(intent);
                }

                return true;
            }
            return false;
        });

        lyOffice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDashboardActivity.this, CustomerEquipmentListActivity.class);
                intent.putExtra("category", tvOffice.getText().toString().toLowerCase());
                startActivity(intent);
            }
        });

        lyElectronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDashboardActivity.this, CustomerEquipmentListActivity.class);
                intent.putExtra("category", tvElectronics.getText().toString().toLowerCase());
                startActivity(intent);
            }
        });

        lyConstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDashboardActivity.this, CustomerEquipmentListActivity.class);
                intent.putExtra("category", tvConstruction.getText().toString().toLowerCase());
                startActivity(intent);
            }
        });

        lyVehicles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomerDashboardActivity.this, CustomerEquipmentListActivity.class);
                intent.putExtra("category", tvVehicles.getText().toString().toLowerCase());
                startActivity(intent);
            }
        });
    }
}