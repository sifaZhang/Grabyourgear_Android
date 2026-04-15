package com.group1.grabyourgear.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.CloudinaryUploader;
import com.group1.grabyourgear.utils.FirebaseHelper_Users;
import com.group1.grabyourgear.utils.UploadCallback;
import com.group1.grabyourgear.utils.UserManager;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private EditText etName, etUsername, etEmail, etPhone, etAddress, etOldPassword, etNewPassword, etReNewPassword, etRole;
    private Button btnSave;
    private ImageView imgAvatarBg;
    private TextView tvChangeAvatar;
    private CloudinaryUploader uploader;
    private String strAvatarUrl;
    private String strUserUID;


    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            uploader.handleResult(data);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Profile");

        etName = findViewById(R.id.etName);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etReNewPassword = findViewById(R.id.etReNewPassword);
        etRole = findViewById(R.id.etRole);
        tvChangeAvatar = findViewById(R.id.tvChangeAvatar);
        btnSave = findViewById(R.id.btnSave);
        imgAvatarBg = findViewById(R.id.imgAvatarBg);

        uploader = new CloudinaryUploader(this, imagePickerLauncher);

        loadUserProfile();

        tvChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploader.pickImage(new UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        showImage(imageUrl);
                        strAvatarUrl = imageUrl;
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(ProfileActivity.this, "Upload failed：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void showImage(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder_avatar)
                .into(imgHeaderAvatar);

        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder_avatar)
                .into(imgAvatarBg);
    }

    private void saveUserProfile() {
        String fullName = etName.getText().toString().trim();
        String oldPassword = etOldPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String newConfirmPassword = etReNewPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String username = etUsername.getText().toString().trim();

        // basic check
        if (username.isEmpty() || phone.isEmpty() || address.isEmpty() || fullName.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // password
        if (!newPassword.equals(newConfirmPassword)) {
            Toast.makeText(ProfileActivity.this, "New Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.isEmpty() && newPassword.equals(oldPassword)) {
            Toast.makeText(ProfileActivity.this, "New Passwords equal old password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.isEmpty() && oldPassword.isEmpty()) {
            Toast.makeText(ProfileActivity.this, "Please input old password", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. update to database
        Map<String, Object> updates = new HashMap<>();
        updates.put(FirebaseNodes.UserFields.FULLNAME, fullName);
        updates.put(FirebaseNodes.UserFields.USERNAME, username);
        updates.put(FirebaseNodes.UserFields.PHONE, phone);
        updates.put(FirebaseNodes.UserFields.ADDRESS, address);
        updates.put(FirebaseNodes.UserFields.AVATAR, strAvatarUrl);

        FirebaseHelper_Users.updateUser(strUserUID, updates, oldPassword, newPassword, new FirebaseHelper_Users.UpdateCallback() {
            @Override
            public void onSuccess() {
                Users user = UserManager.getInstance().getUser();
                if (user != null) {
                    user.setName(fullName);
                    user.setUsername(username);
                    user.setPhone(phone);
                    user.setAddress(address);
                    user.setAvatar(strAvatarUrl);
                    UserManager.getInstance().setUser(user);
                    etOldPassword.setText("");
                    etNewPassword.setText("");
                    etReNewPassword.setText("");
                    Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "User is null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                if (e instanceof FirebaseAuthInvalidCredentialsException ||
                        e instanceof IllegalArgumentException) {
                    Toast.makeText(ProfileActivity.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadUserProfile() {
        Users user = UserManager.getInstance().getUser();
        if(user == null || user.getUid().isEmpty()) {
            Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show();
        } else {
            etName.setText(user.getName());
            etUsername.setText(user.getUsername());
            etEmail.setText(user.getEmail());
            etPhone.setText(user.getPhone());
            etAddress.setText(user.getAddress());
            etRole.setText(user.getRole());
            strAvatarUrl = user.getAvatar();
            strUserUID = user.getUid();

            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                showImage(user.getAvatar());
            }
        }
    }
}