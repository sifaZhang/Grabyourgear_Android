package com.group1.grabyourgear.utils;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.customer.CustomerEquipmentDetailActivity;
import com.group1.grabyourgear.models.Equipment;

import java.text.DecimalFormat;
import java.util.List;

public class Adapter_EquipmentView extends RecyclerView.Adapter<Adapter_EquipmentView.EquipmentViewHolder> {
        private Context context;
        private List<Equipment> equipmentList;

        public Adapter_EquipmentView(Context context, List<Equipment> equipmentList) {
            this.context = context;
            this.equipmentList = equipmentList;
        }

        @NonNull
        @Override
        public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_common_equipment, parent, false);
            return new EquipmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EquipmentViewHolder holder, int position) {
            Equipment item = equipmentList.get(position);

            DecimalFormat df = new DecimalFormat("0.00");
            holder.tvTitle.setText(item.getName());
            holder.tvCategory.setText(CategoryRepository.getInstance().getCategoryName(item.getCategoryId()));
            if (item.getRateCount() == 0) {
                holder.tvRating.setText(Html.fromHtml("<b>Rating:</b> (No rate yet)", Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.tvRating.setText(Html.fromHtml("<b>Rating:</b> " + df.format(item.getRating()) + " / 5 (" + item.getRateCount() + " ratings)", Html.FROM_HTML_MODE_LEGACY));
            }
            holder.tvLocation.setText(Html.fromHtml("<b>Location:</b> " + item.getLocation(), Html.FROM_HTML_MODE_LEGACY));
            holder.tvPrice.setText(Html.fromHtml("<b>Price:</b> $" + df.format(item.getPricePerDay()) + " / day", Html.FROM_HTML_MODE_LEGACY));
            holder.tvDiscount.setText(Html.fromHtml("<b>Discount:</b> " + Math.round(item.getDiscount()) + "% OFF", Html.FROM_HTML_MODE_LEGACY));

            // Load image
            Glide.with(context)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.placeholder_general)
                    .into(holder.imgEquipment);

            // Click event
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CustomerEquipmentDetailActivity.class);
                intent.putExtra(AppConstants.IntenParamer.EQUIPMENT_ID, item.getId());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return equipmentList.size();
        }

        public static class EquipmentViewHolder extends RecyclerView.ViewHolder {

            ImageView imgEquipment;
            TextView tvCategory, tvTitle, tvRating, tvLocation, tvPrice, tvDiscount;

            public EquipmentViewHolder(@NonNull View itemView) {
                super(itemView);

                imgEquipment = itemView.findViewById(R.id.imgEquipment);
                tvCategory = itemView.findViewById(R.id.tvItemCategory);
                tvTitle = itemView.findViewById(R.id.tvItemTitle);
                tvRating = itemView.findViewById(R.id.tvItemRating);
                tvLocation = itemView.findViewById(R.id.tvItemLocation);
                tvPrice = itemView.findViewById(R.id.tvItemPrice);
                tvDiscount = itemView.findViewById(R.id.tvItemDiscount);
            }
        }
    }

