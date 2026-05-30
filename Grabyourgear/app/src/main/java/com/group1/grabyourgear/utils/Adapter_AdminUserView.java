package com.group1.grabyourgear.utils;

import android.content.Context;
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
import com.group1.grabyourgear.models.Users;

import java.util.List;

public class Adapter_AdminUserView extends RecyclerView.Adapter<Adapter_AdminUserView.AdminUserViewHolder> {

    private Context context;

    private List<Users> usersList;

    public Adapter_AdminUserView(Context context, List<Users> userList) {
        this.context = context;
        this.usersList = userList;
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_user,
                parent, false);

        return new AdminUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {
        Users user = usersList.get(position);

        holder.tvName.setText(user.getName());
        holder.tvUserName.setText(user.getUsername());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getPhone());
        holder.tvAddress.setText(user.getAddress());

        Glide.with(context)
                .load(user.getAvatar())
                .placeholder(R.drawable.placeholder_avatar)
                .into(holder.imgAvatar);

        //TODO: Button handlers
    }

    @Override
    public int getItemCount() { return usersList.size(); }

    public static class AdminUserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;

        TextView tvName, tvUserName, tvEmail, tvPhone, tvAddress;

        Button btnEdit, btnBan;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.img_admin_user_avatar);
            tvName = itemView.findViewById(R.id.tv_admin_user_name);
            tvUserName = itemView.findViewById(R.id.tv_admin_user_username);
            tvEmail = itemView.findViewById(R.id.tv_admin_user_email);
            tvPhone = itemView.findViewById(R.id.tv_admin_user_phone);
            tvAddress = itemView.findViewById(R.id.tv_admin_user_address);

            btnEdit = itemView.findViewById(R.id.btn_admin_user_edit);
            btnBan = itemView.findViewById(R.id.btn_admin_user_ban);
        }
    }
}
