package com.group1.grabyourgear.customer;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Booking;
import com.group1.grabyourgear.models.Equipment;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.EquipmentRepository;
import com.group1.grabyourgear.utils.EquipmentView_Adapter;
import com.group1.grabyourgear.utils.FirebaseHelper_Bookings;
import com.group1.grabyourgear.utils.MyBookingView_Adapter;
import com.group1.grabyourgear.utils.UserManager;

import java.util.List;

public class CustomerMyBookingsActivity extends BaseActivity {
    RecyclerView recyclerView;
    MyBookingView_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_my_bookings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("My Bookings");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Users user = UserManager.getInstance().getUser();
        if (user == null){
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }

        FirebaseHelper_Bookings.loadBookingsByUserId(user.getUid(), new FirebaseHelper_Bookings.BookingListCallback() {
            @Override
            public void onSuccess(List<Booking> bookingList) {
                // 遍历所有 Booking
                adapter = new MyBookingView_Adapter(
                        CustomerMyBookingsActivity.this,
                        bookingList,
                        EquipmentRepository.getInstance().getCachedEquipment(),
                        booking -> {
                            // 点击 Cancel 后执行的逻辑
                            handleCancelBooking(booking);
                        }
                );

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CustomerMyBookingsActivity.this,
                        "Failed to load bookings: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleCancelBooking(Booking booking) {
            new AlertDialog.Builder(CustomerMyBookingsActivity.this)
                    .setTitle("Confirmation")
                    .setMessage("Ary you sure to cancel this booking?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseHelper_Bookings.updateBookingStatus(
                                booking.getId(),
                                FirebaseNodes.BookingStatus.CANCELLED,
                                new FirebaseHelper_Bookings.OnBookingUpdateListener() {
                                    @Override
                                    public void onSuccess() {

                                        booking.setStatus(FirebaseNodes.BookingStatus.CANCELLED);

                                        int index = adapter.getBookingIndex(booking.getId());
                                        adapter.notifyItemChanged(index);

                                        Toast.makeText(
                                                CustomerMyBookingsActivity.this,
                                                "Booking cancelled",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(
                                                CustomerMyBookingsActivity.this,
                                                "Failed: " + error,
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                        );
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();

    }

}