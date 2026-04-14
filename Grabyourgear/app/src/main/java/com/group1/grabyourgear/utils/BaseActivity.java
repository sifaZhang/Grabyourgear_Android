package com.group1.grabyourgear.utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.admin.AdminDashboardActivity;
import com.group1.grabyourgear.auth.LoginActivity;
import com.group1.grabyourgear.auth.ProfileActivity;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.customer.CustomerDashboardActivity;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.service.ServiceDashboardActivity;
import com.group1.grabyourgear.supplier.SupplierDashboardActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected ImageView imgAvatar, imgLogo;
    protected TextView tvTitle, tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        setupHeader();
    }


    protected void setupHeader() {
        imgAvatar = findViewById(R.id.imgAvatar);
        tvTitle = findViewById(R.id.tvTitle);
        imgLogo = findViewById(R.id.imgLogo);
        tvUsername = findViewById(R.id.tvUsername);

        if (imgAvatar == null || tvTitle == null || imgLogo == null || tvUsername == null) {
            return;
        }

        setupAvatarClick();
        loadUserAvatar();
        setupLogoClicke();
        setupUsername();
    }

    private void setupUsername() {
        Users user = UserManager.getInstance().getUser();
        if (user != null) {
            tvUsername.setText(user.username);
        }
    }

    private void setupLogoClicke() {
        imgLogo.setOnClickListener(v -> {
            Users user = UserManager.getInstance().getUser();
            if (user != null && user.role != null) {
                goToDashboard(user.role);
            }
        });
    }

    private void setupAvatarClick() {
        imgAvatar.setOnClickListener(v -> {
            UserPrefs prefs = new UserPrefs(this);
            if (prefs.isLoggedIn()) {
                showAvatarMenu(v);
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    protected void goToDashboard(String role) {
        switch (role) {
            case AppConstants.Role.CUSTOMER:
                startActivity(new Intent(this, CustomerDashboardActivity.class));
                finish();
                break;
            case AppConstants.Role.SUPPLIER:
                startActivity(new Intent(this, SupplierDashboardActivity.class));
                finish();
                break;
            case AppConstants.Role.SERVICE:
                startActivity(new Intent(this, ServiceDashboardActivity.class));
                finish();
                break;
            case AppConstants.Role.ADMIN:
                startActivity(new Intent(this, AdminDashboardActivity.class));
                finish();
                break;
            default:
                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showAvatarMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.avatar_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.menu_logout) {
                UserPrefs prefs = new UserPrefs(this);
                prefs.clear();
                UserManager.getInstance().clear();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void loadUserAvatar() {
        Users user = UserManager.getInstance().getUser();
        if(user == null || user.avatar == null || user.avatar.isEmpty())
            return;

        Glide.with(this)
                .load(user.avatar)
                .placeholder(R.drawable.placeholder_avatar)
                .into(imgAvatar);
    }

    protected void setHeaderTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }
}
