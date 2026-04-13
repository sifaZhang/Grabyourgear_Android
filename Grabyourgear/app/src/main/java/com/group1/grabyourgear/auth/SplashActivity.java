package com.group1.grabyourgear.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.admin.AdminDashboardActivity;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.customer.CustomerDashboardActivity;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.service.ServiceDashboardActivity;
import com.group1.grabyourgear.supplier.SupplierDashboardActivity;
import com.group1.grabyourgear.utils.FirebaseHelper_Users;
import com.group1.grabyourgear.utils.UserManager;
import com.group1.grabyourgear.utils.UserPrefs;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UserPrefs prefs = new UserPrefs(this);
        String uid = prefs.getUid();

        if (!prefs.isLoggedIn() || uid == null) {
            goToLogin();
            return;
        }

        Users currentUser = UserManager.getInstance().getUser();

        if (currentUser == null) {
            FirebaseHelper_Users.loadUserInfo(uid, new FirebaseHelper_Users.UserCallback() {
                @Override
                public void onSuccess(Users user) {
                    UserManager.getInstance().setUser(user);
                    goToDashboard(user.role);
                }

                @Override
                public void onFailure(Exception e) {
                    goToLogin(); // 数据损坏或网络问题
                }
            });
        }
        else {
            goToDashboard(currentUser.role);
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private void goToDashboard(String role) {
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
                goToLogin();
                break;
        }
    }

}