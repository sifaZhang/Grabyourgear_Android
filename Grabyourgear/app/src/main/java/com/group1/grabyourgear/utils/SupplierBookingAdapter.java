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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupplierBookingAdapter extends RecyclerView.Adapter<SupplierBookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public SupplierBookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public SupplierBookingAdapter.BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_supplier_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierBookingAdapter.BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvTitle.setText("Equipment ID: " + booking.getEquipmentId());

        holder.tvDates.setText(Html.fromHtml(
                "<b>Dates:</b> " + formatDate(booking.getStartDate()) + " - " + formatDate(booking.getEndDate()),
                Html.FROM_HTML_MODE_LEGACY
        ));

        holder.tvCustomer.setText(Html.fromHtml(
                "<b>Customer:</b> " + booking.getUserId(),
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

    private void updateStatus(String bookingId, String newStatus) {
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
        TextView tvTitle, tvDates, tvCustomer, tvPrice, tvStatus;
        Button btnApprove, btnReject;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            imgEquipment = itemView.findViewById(R.id.imgEquipBooking);

            tvTitle = itemView.findViewById(R.id.tvItemTitleBooking);
            tvDates = itemView.findViewById(R.id.tvDateRangeBooking);
            tvCustomer = itemView.findViewById(R.id.tvCustomerBooking);
            tvPrice = itemView.findViewById(R.id.tvItemPriceBooking);
            tvStatus = itemView.findViewById(R.id.tvItemStatusBooking);

            btnApprove = itemView.findViewById(R.id.btnApproveBooking);
            btnReject = itemView.findViewById(R.id.btnRejectBooking);
        }
    }
}
