package com.group1.grabyourgear.utils;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.auth.LoginActivity;
import com.group1.grabyourgear.auth.ProfileActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected ImageView imgAvatar;
    protected TextView tvTitle;

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

        if (imgAvatar == null || tvTitle == null) {
            return;
        }

        setupAvatarClick();
        loadUserAvatar();
    }

    private void setupAvatarClick() {
        imgAvatar.setOnClickListener(v -> {
            UserPrefs prefs = new UserPrefs(this);
            if (prefs.isLoggedIn()) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    private void loadUserAvatar() {
        UserPrefs prefs = new UserPrefs(this);
        if (!prefs.isLoggedIn())return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(prefs.getUid());

        ref.child("avatar").get().addOnSuccessListener(snapshot -> {
            String url = snapshot.getValue(String.class);
            if (url != null && !url.isEmpty()) {
                Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.placeholder_avatar)
                        .into(imgAvatar);
            }
        });
    }

    protected void setHeaderTitle(String title) {
        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }
}
