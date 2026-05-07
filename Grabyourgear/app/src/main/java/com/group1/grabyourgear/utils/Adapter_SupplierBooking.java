package com.group1.grabyourgear.utils;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.models.Booking;
import com.group1.grabyourgear.models.Equipment;
import com.group1.grabyourgear.models.Users;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter_SupplierBooking extends RecyclerView.Adapter<Adapter_SupplierBooking.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public Adapter_SupplierBooking(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public Adapter_SupplierBooking.BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_supplier_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_SupplierBooking.BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvTitle.setText("Loading equipment...");
        holder.tvEquipmentId.setText("ID: " + booking.getEquipmentId());
        holder.tvCustomer.setText("Loading customer...");

        holder.tvDates.setText(Html.fromHtml(
                "<b>Dates:</b> " + formatDate(booking.getStartDate()) + " - " + formatDate(booking.getEndDate()),
                Html.FROM_HTML_MODE_LEGACY
        ));

        holder.tvPrice.setText(Html.fromHtml(
                "<b>Total:</b> " + booking.getTotalPrice(),
                Html.FROM_HTML_MODE_LEGACY
        ));

        holder.tvStatus.setText(booking.getStatus());

        Glide.with(context)
                .load(R.drawable.placeholder_general)
                .into(holder.imgEquipment);

        loadEquipmentDetails(holder, booking.getEquipmentId());
        loadCustomerDetails(holder, booking.getUserId());

        boolean isPending = "pending".equalsIgnoreCase(booking.getStatus());

        holder.btnApprove.setVisibility(isPending ? View.VISIBLE : View.GONE);
        holder.btnReject.setVisibility(isPending ? View.VISIBLE : View.GONE);

        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(booking.getId(), "approved");
            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(booking.getId(), "rejected");
            }
        });
    }

    private void loadEquipmentDetails(BookingViewHolder holder, String equipmentId) {
        FirebaseDatabase.getInstance()
                .getReference("equipment")
                .child(equipmentId)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    Equipment equipment = dataSnapshot.getValue(Equipment.class);

                    if(equipment != null) {
                        holder.tvTitle.setText(equipment.getName());

                        Glide.with(context)
                                .load(equipment.getImageUrl())
                                .placeholder(R.drawable.placeholder_general)
                                .into(holder.imgEquipment);
                    } else {
                        holder.tvTitle.setText("Unknown Equipment");
                    }
                })
                .addOnFailureListener(e -> {
                    holder.tvTitle.setText("Failed to load Equipment");
                });
    }

    private void loadCustomerDetails(BookingViewHolder holder, String userId) {
        FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    Users user = dataSnapshot.getValue(Users.class);

                    if(user != null) {
                        String displayName;

                        if(user.getName() != null && !user.getName().isEmpty()) {
                            displayName = user.getName();
                        } else if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                            displayName = user.getUsername();
                        } else {
                            displayName = user.getEmail();
                        }

                        holder.tvCustomer.setText(Html.fromHtml(
                                "<b>Customer:</b> " + displayName,
                                Html.FROM_HTML_MODE_LEGACY
                        ));
                    } else {
                        holder.tvCustomer.setText(Html.fromHtml(
                                "<b>Customer:</b> Unknown",
                                Html.FROM_HTML_MODE_LEGACY
                        ));
                    }
                })
                .addOnFailureListener(e -> {
                    holder.tvCustomer.setText(Html.fromHtml(
                            "<b>Customer></b> Failed to load",
                            Html.FROM_HTML_MODE_LEGACY
                    ));
                });
    }

    private void updateStatus(String bookingId, String newStatus) {
        if (bookingId == null || bookingId.isEmpty()) {
            Toast.makeText(context, "Booking ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase.getInstance()
                .getReference("bookings")
                .child(bookingId)
                .child("status")
                .setValue(newStatus)
                .addOnSuccessListener(unused ->
                    Toast.makeText(context, "Booking " + newStatus, Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {

        ImageView imgEquipment;
        TextView tvTitle, tvEquipmentId, tvDates, tvCustomer, tvPrice, tvStatus;
        Button btnApprove, btnReject;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            imgEquipment = itemView.findViewById(R.id.imgEquipBooking);

            tvTitle = itemView.findViewById(R.id.tvItemTitleBooking);
            tvEquipmentId = itemView.findViewById(R.id.tvEquipmentIdBooking);
            tvDates = itemView.findViewById(R.id.tvDateRangeBooking);
            tvCustomer = itemView.findViewById(R.id.tvCustomerBooking);
            tvPrice = itemView.findViewById(R.id.tvItemPriceBooking);
            tvStatus = itemView.findViewById(R.id.tvItemStatusBooking);

            btnApprove = itemView.findViewById(R.id.btnApproveBooking);
            btnReject = itemView.findViewById(R.id.btnRejectBooking);
        }
    }
}
