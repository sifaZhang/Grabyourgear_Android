package com.group1.grabyourgear.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Equipment;
import com.group1.grabyourgear.supplier.SupplierEditEquipmentActivity;
import com.group1.grabyourgear.supplier.SupplierEquipmentDetailActivity;

import java.text.DecimalFormat;
import java.util.List;

public class Adapter_SupplierEquipmentView extends RecyclerView.Adapter<Adapter_SupplierEquipmentView.SupplierEquipmentViewHolder> {
    private Context context;
    private List<Equipment> equipmentList;

    public Adapter_SupplierEquipmentView(Context context, List<Equipment> equipmentList) {
        this.context = context;
        this.equipmentList = equipmentList;
    }

    @NonNull
    @Override
    public SupplierEquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_supplier_equipment, parent, false);
        return new SupplierEquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplierEquipmentViewHolder holder, int position) {
        Equipment item = equipmentList.get(position);

        DecimalFormat df = new DecimalFormat("0.00");
        holder.tvTitle.setText(item.getName());
        holder.tvCategory.setText(CategoryRepository.getInstance().getCategoryName(item.getCategoryId()));
        //holder.tvRating.setText(Html.fromHtml("<b>Rating:</b> " + item.getRating(), Html.FROM_HTML_MODE_LEGACY));
        holder.tvLocation.setText(Html.fromHtml("<b>Location:</b> " + item.getLocation(), Html.FROM_HTML_MODE_LEGACY));
        holder.tvPrice.setText(Html.fromHtml("<b>Price:</b> $" + df.format(item.getPricePerDay()) + " / day", Html.FROM_HTML_MODE_LEGACY));
        holder.tvDiscount.setText(Html.fromHtml("<b>Discount:</b> " + Math.round(item.getDiscount()) + "% OFF", Html.FROM_HTML_MODE_LEGACY));
        holder.tvStatus.setText(Html.fromHtml("<b>Status:</b> " + item.getStatus()));

        // Load image
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_general)
                .into(holder.imgEquipment);

        // Edit
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, SupplierEditEquipmentActivity.class);
            intent.putExtra(AppConstants.IntenParamer.EQUIPMENT_ID, item.getId());
            context.startActivity(intent);
        });

        // Delete
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Equipment")
                    .setMessage("Are you sure you want to delete this equipment?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        String equipmentId = item.getId();

                        FirebaseDatabase.getInstance()
                                .getReference(FirebaseNodes.EQUIPMENT)
                                .child(equipmentId)
                                .removeValue()
                                .addOnSuccessListener(unused -> {

                                    deleteRelatedBookings(equipmentId, () -> {
                                        int currentPosition = holder.getAdapterPosition();

                                        if (currentPosition != RecyclerView.NO_POSITION) {
                                            equipmentList.remove(currentPosition);
                                            notifyItemRemoved(currentPosition);
                                            notifyItemRangeChanged(currentPosition, equipmentList.size());
                                        }

                                        Toast.makeText(context, "Equipment and related bookings deleted", Toast.LENGTH_SHORT).show();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SupplierEquipmentDetailActivity.class);
                intent.putExtra(AppConstants.IntenParamer.EQUIPMENT_ID, item.getId());
                intent.putExtra("mode", "equipment");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return equipmentList.size();
    }

    private void deleteRelatedBookings(String equipmentId, Runnable onComplete) {
        FirebaseDatabase.getInstance()
                .getReference(FirebaseNodes.BOOKINGS)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    for(DataSnapshot bookingSnapshot : dataSnapshot.getChildren()) {
                        String bookingEquipmentId = bookingSnapshot
                                .child(FirebaseNodes.BookingsFields.EQUIPMENT_ID)
                                .getValue(String.class);

                        if(equipmentId.equals(bookingEquipmentId)) {
                            bookingSnapshot.getRef().removeValue();
                        }
                    }

                    onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            context,
                            "Equipment deleted, but bookings could not be checked.",
                            Toast.LENGTH_SHORT
                    ).show();

                    onComplete.run();
                });
    }

    public static class SupplierEquipmentViewHolder extends RecyclerView.ViewHolder {

        ImageView imgEquipment;
        TextView tvCategory, tvTitle, tvLocation, tvPrice, tvStatus, tvDiscount;
        Button btnEdit, btnDelete;

        public SupplierEquipmentViewHolder(@NonNull View itemView) {
            super(itemView);

            imgEquipment = itemView.findViewById(R.id.imgEquipmentSupplier);
            tvCategory = itemView.findViewById(R.id.tvItemCategorySupplier);
            tvTitle = itemView.findViewById(R.id.tvItemTitleSupplier);
            //tvRating = itemView.findViewById(R.id.tvItemRating);
            tvLocation = itemView.findViewById(R.id.tvItemLocationSupplier);
            tvPrice = itemView.findViewById(R.id.tvItemPriceSupplier);
            tvStatus = itemView.findViewById(R.id.tvItemStatusSupplier);
            tvDiscount = itemView.findViewById(R.id.tvItemDiscount);

            btnEdit = itemView.findViewById(R.id.btnEditEquipment);
            btnDelete = itemView.findViewById(R.id.btnDeleteEquipment);
        }
    }
}

