package com.group1.grabyourgear.utils;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Booking;
import com.group1.grabyourgear.models.Equipment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Adapter_MyBookingView extends RecyclerView.Adapter<Adapter_MyBookingView.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private List<Equipment> equipmentList; // 需要设备信息来显示图片、标题等
    private OnCancelClickListener cancelClickListener;
    private OnRateClickListener rateClickListener;

    public interface OnCancelClickListener {
        void onCancel(Booking booking);
    }

    public interface OnRateClickListener {
        void onRate(Booking booking);
    }

    public Adapter_MyBookingView(Context context, List<Booking> bookingList,
                                 List<Equipment> equipmentList,
                                 OnCancelClickListener cancelClickListener,
                                 OnRateClickListener rateClickListener) {
        this.context = context;
        this.bookingList = bookingList;
        this.equipmentList = equipmentList;
        this.cancelClickListener = cancelClickListener;
        this.rateClickListener = rateClickListener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_booked_equipment, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // 找到对应的设备信息
        Equipment equipment = null;
        for (Equipment e : equipmentList) {
            if (e.getId().equals(booking.getEquipmentId())) {
                equipment = e;
                break;
            }
        }

        if (equipment != null) {
            holder.tvItemTitle.setText(equipment.getName());
            holder.tvItemCategory.setText(CategoryRepository.getInstance().getCategoryName(equipment.getCategoryId()));
            holder.tvItemLocation.setText(Html.fromHtml("<b>Location: </b>"  +equipment.getLocation(), Html.FROM_HTML_MODE_LEGACY));
            holder.tvItemPrice.setText(
                    Html.fromHtml("<b>TotalPrice: $</b>" + String.format("%.2f", booking.getTotalPrice()),
                            Html.FROM_HTML_MODE_LEGACY)
            );
            holder.tvItemStatus.setText(Html.fromHtml("<b>Status: </b>"  + booking.getStatus(), Html.FROM_HTML_MODE_LEGACY));
            if (booking.getRating() == 0) {
                holder.tvItemRate.setText(Html.fromHtml("<b>Your Rating: </b>" + "(No rate yet)", Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.tvItemRate.setText(Html.fromHtml("<b>Your Rating: </b>" + String.valueOf(booking.getRating()) + " / 5", Html.FROM_HTML_MODE_LEGACY));
            }
            holder.btnCancel.setVisibility(booking.getStatus().equals(FirebaseNodes.BookingStatus.PENDING) ? View.VISIBLE : View.GONE);
            holder.btnRate.setVisibility(booking.getStatus().equals(FirebaseNodes.BookingStatus.COMPLETED) ? View.VISIBLE : View.GONE);

            Glide.with(context)
                    .load(equipment.getImageUrl())
                    .placeholder(R.drawable.placeholder_general)
                    .into(holder.imgEquipment);
        }

        // 日期格式化
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Spanned dateRange = Html.fromHtml( "<b>Date: </b>" + sdf.format(new Date(booking.getStartDate())) + " - " +
                sdf.format(new Date(booking.getEndDate())), Html.FROM_HTML_MODE_LEGACY);
        holder.tvDateRange.setText(dateRange);

        // Cancel 按钮
        holder.btnCancel.setOnClickListener(v -> {
            if (cancelClickListener != null) {
                cancelClickListener.onCancel(booking);
            }
        });

        holder.btnRate.setOnClickListener(v -> {
            if (rateClickListener != null) {
                rateClickListener.onRate(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public int getBookingIndex(String bookingId) {
        for (int i = 0; i < bookingList.size(); i++) {
            if (bookingList.get(i).getId().equals(bookingId)) {
                return i;
            }
        }
        return -1; // 没找到
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView imgEquipment;
        TextView tvItemCategory, tvItemTitle, tvDateRange, tvItemLocation, tvItemPrice, tvItemStatus, tvItemRate;
        Button btnCancel, btnRate;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);

            imgEquipment = itemView.findViewById(R.id.imgEquipment);
            tvItemRate = itemView.findViewById(R.id.tvItemRate);
            tvItemCategory = itemView.findViewById(R.id.tvItemCategory);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            tvItemLocation = itemView.findViewById(R.id.tvItemLocation);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnRate = itemView.findViewById(R.id.btnRate);
        }
    }

    public void updateBooking(Booking updatedBooking) {
        for (int i = 0; i < bookingList.size(); i++) {
            if (bookingList.get(i).getId().equals(updatedBooking.getId())) {
                bookingList.set(i, updatedBooking);
                notifyItemChanged(i);
                break;
            }
        }
    }
}
