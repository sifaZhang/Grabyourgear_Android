package com.group1.grabyourgear.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.FirebaseHelper;

public class RegisterActivity extends BaseActivity {

    EditText etName, etUsername, etEmail, etPassword, etRePassword, etPhone, etAddress;
    Spinner spRole;
    Button btnRegister;
    TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRegister = findViewById(R.id.btnRegister);
        etAddress = findViewById(R.id.etAddress);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        etPhone = findViewById(R.id.etPhone);
        etRePassword = findViewById(R.id.etRePassword);
        etUsername = findViewById(R.id.etUsername);
        spRole = findViewById(R.id.spRole);
        tvLogin = findViewById(R.id.tvLogin);

        tvLogin.setText(Html.fromHtml("Already have an account? <u>Login</u>",
                Html.FROM_HTML_MODE_LEGACY));

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etRePassword.getText().toString().trim();
                String UIRole = spRole.getSelectedItem().toString();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String sysRole = convertRole(UIRole);
                boolean isApproved = isApproved(sysRole);

                // basic check
                if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Email
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                    return;
                }

                // password
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                //save to database
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {

                            String uid = authResult.getUser().getUid();

                            Users user = new Users(
                                    uid,
                                    fullName,
                                    username,
                                    email,
                                    phone,
                                    address,
                                    sysRole,
                                    "",
                                    isApproved
                            );

                            FirebaseHelper.registerUser(user, new FirebaseHelper.RegisterCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();

                                    //go to login
                                    finish();
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(RegisterActivity.this, "DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        })
                        .addOnFailureListener(e -> {
                            Log.e("Register", "Auth error: " + e.getMessage(), e);
                            Toast.makeText(RegisterActivity.this, "Auth Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // go to  Login page
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String convertRole(String role) {
        switch (role) {
            case "Customer":
                return AppConstants.Role.CUSTOMER;
            case "Supplier":
                return AppConstants.Role.SUPPLIER;
            case "Administrator":
                return AppConstants.Role.ADMIN;
            case "Customer Service":
                return AppConstants.Role.SERVICE;
            default:
                return "";
        }
    }

    private boolean isApproved(String role) {
        return role.equals(AppConstants.Role.CUSTOMER);
    }
}