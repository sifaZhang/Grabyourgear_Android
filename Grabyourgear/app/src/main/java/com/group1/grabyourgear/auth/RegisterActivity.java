package com.group1.grabyourgear.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.FirebaseHelper_Users;

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends BaseActivity {

    private EditText etName, etUsername, etEmail, etPassword, etRePassword, etPhone, etAddress;
    private Spinner spRole;
    private Button btnRegister;
    private TextView tvLogin;

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

        setHeaderTitle("Registration");

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

        imgAvatar.setVisibility(View.INVISIBLE);
        imgLogo.setVisibility(View.INVISIBLE);

        tvLogin.setText(Html.fromHtml("Already have an account? <u>Login</u>",
                Html.FROM_HTML_MODE_LEGACY));

        List<String> roles = Arrays.asList(AppConstants.Role.CUSTOMER, AppConstants.Role.SUPPLIER, AppConstants.Role.ADMIN, AppConstants.Role.SERVICE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                roles
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fullName = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etRePassword.getText().toString().trim();
                String role = spRole.getSelectedItem().toString();
                String phone = etPhone.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                boolean isApproved = isApproved(role);

                // basic check
                if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                        || role.isEmpty() || phone.isEmpty() || address.isEmpty() || fullName.isEmpty()) {
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

                Users user = new Users(
                        null,       // uid 由 FirebaseAuth 创建后再写入
                        fullName,
                        username,
                        email,
                        phone,
                        address,
                        role,
                        "",
                        isApproved
                );

                //save to database
                FirebaseHelper_Users.registerUserWithAuth(
                        email,
                        password,
                        user,
                        new FirebaseHelper_Users.RegisterCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(RegisterActivity.this, "Register failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
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

    private boolean isApproved(String role) {
        return role.equals(AppConstants.Role.CUSTOMER);
    }
}