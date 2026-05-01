package com.group1.grabyourgear.supplier;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.SupplierEquipmentViewAdapter;
import com.group1.grabyourgear.utils.UserManager;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.models.Equipment;

import java.util.ArrayList;
import java.util.List;

public class SupplierMyEquipmentActivity extends BaseActivity {
    RecyclerView recyclerView;
    List<Equipment> equipmentList;

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

        setHeaderTitle("My Products");

        recyclerView = findViewById(R.id.rvMyProductSupplier);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        equipmentList = new ArrayList<>();

        loadMyEquipment();
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
                        equipmentList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Equipment equipment = ds.getValue(Equipment.class);
                            if(equipment != null) {
                                equipmentList.add(equipment);
                            }
                        }

                        SupplierEquipmentViewAdapter adapter = new SupplierEquipmentViewAdapter(
                                SupplierMyEquipmentActivity.this,
                                equipmentList
                        );

                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SupplierMyEquipmentActivity.this,
                                "Failed to load products.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }
}