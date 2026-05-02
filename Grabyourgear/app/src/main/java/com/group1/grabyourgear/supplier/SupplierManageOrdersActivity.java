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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.models.Booking;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.SupplierBookingAdapter;

import java.util.ArrayList;
import java.util.List;

public class SupplierManageOrdersActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private List<Booking> bookingList;

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

        bookingList = new ArrayList<>();

        loadSupplierBookings();
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
                        bookingList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Booking booking = ds.getValue(Booking.class);

                            if(booking != null) {
                                booking.setId(ds.getKey());
                                bookingList.add(booking);
                            }
                        }

                        SupplierBookingAdapter adapter = new SupplierBookingAdapter(
                                SupplierManageOrdersActivity.this, bookingList
                        );

                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SupplierManageOrdersActivity.this,
                                "Failed to load orders.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}