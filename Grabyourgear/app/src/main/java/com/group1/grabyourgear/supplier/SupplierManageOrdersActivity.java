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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.models.Booking;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.SupplierBookingAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SupplierManageOrdersActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<Booking> allBookingList, filteredBookingList;
    private EditText etKeyword;
    private Spinner spStatus;
    private Button btnSearch, btnClear;
    private SupplierBookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_manage_orders);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Manage Orders");

        recyclerView = findViewById(R.id.rvSupplierOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spStatus = findViewById(R.id.spFilterStatusOrdersSupplier);
        etKeyword = findViewById(R.id.etFilterKeywordSupplierOrders);
        btnSearch = findViewById(R.id.btnSearchOrdersSupplier);
        btnClear = findViewById(R.id.btnClearOrdersSupplier);

        allBookingList = new ArrayList<>();
        filteredBookingList = new ArrayList<>();

        setupFilters();

        adapter = new SupplierBookingAdapter(this, filteredBookingList);
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
                spStatus.setSelection(0);
                etKeyword.setText("");

                filteredBookingList.clear();
                filteredBookingList.addAll(allBookingList);
                adapter.notifyDataSetChanged();
            }
        });

        loadSupplierBookings();
    }

    private void setupFilters() {
        List <String> statuses = Arrays.asList(
                "All",
                "pending",
                "approved",
                "rejected"
        );

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statuses
        );

        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(statusAdapter);
    }

    private void loadSupplierBookings() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String supplierId = user.getUid();

        FirebaseDatabase.getInstance()
                .getReference("bookings")
                .orderByChild("supplierId")
                .equalTo(supplierId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        allBookingList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Booking booking = ds.getValue(Booking.class);
                            if(booking != null) {
                                booking.setId(ds.getKey());
                                allBookingList.add(booking);
                            }
                        }

                        filteredBookingList.clear();
                        filteredBookingList.addAll(allBookingList);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SupplierManageOrdersActivity.this,
                                "Failed to load orders.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void applySearch() {
        filteredBookingList.clear();

        String selectedStatus = spStatus.getSelectedItem().toString();
        String keyword = etKeyword.getText().toString().trim().toLowerCase();

        for(Booking booking : allBookingList) {
            boolean matchesStatus = selectedStatus.equals("All")
                    || (booking.getStatus() != null
            && booking.getStatus().equalsIgnoreCase(selectedStatus));

            boolean matchesKeyword = keyword.isEmpty()
                    || containsIgnoreCase(booking.getId(), keyword)
                    || containsIgnoreCase(booking.getEquipmentId(), keyword)
                    || containsIgnoreCase(booking.getUserId(), keyword)
                    || containsIgnoreCase(booking.getStatus(), keyword);

            if(matchesStatus && matchesKeyword) {
                filteredBookingList.add(booking);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }
}