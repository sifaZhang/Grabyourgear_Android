package com.group1.grabyourgear.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.models.Users;

import java.util.List;

/* This will be for the user view, not finished at all yet, I kept getting sidetracked with stuff
   like this instead of focusing on the supplier applications activity. Oops.
public class Adapter_AdminUserView extends RecyclerView.Adapter<Adapter_AdminUserView.AdminUserViewHolder> {

    private Context context;

    private List<Users> userList;

    public Adapter_AdminUserView(Context context, List<Users> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public AdminUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    }

    @Override
    public void onBindViewHolder(@NonNull AdminUserViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() { return userList.size(); }

    public static class AdminUserViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;

        TextView tvName, tvUserName, tvAddress, tvEmail, tvPhone;

        public AdminUserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = itemView.findViewById(R.id.img_admin_user_avatar);

        }
    }
}
*/