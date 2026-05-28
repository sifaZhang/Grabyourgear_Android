package com.group1.grabyourgear.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.utils.Adapter_AdminSupplierApplicationView;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.FirebaseHelper_Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminReviewSuppliersActivity extends BaseActivity {

    RecyclerView recyclerView;
    Adapter_AdminSupplierApplicationView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_review_suppliers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Supplier Applications");

        recyclerView = findViewById(R.id.rv_applications_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseHelper_Users.loadAllUsers(new FirebaseHelper_Users.UserListCallback() {
            @Override
            public void onSuccess(List<Users> usersList) {
                List<Users> applicationsList = new ArrayList<>();

                for (Users u : usersList) {
                    if (Objects.equals(u.getRole(), "supplier") && !u.isApproved()) {
                        applicationsList.add(u);
                    }
                }

                adapter = new Adapter_AdminSupplierApplicationView(
                        AdminReviewSuppliersActivity.this,
                        applicationsList);

                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminReviewSuppliersActivity.this,
                        "Supplier application retrieval failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}