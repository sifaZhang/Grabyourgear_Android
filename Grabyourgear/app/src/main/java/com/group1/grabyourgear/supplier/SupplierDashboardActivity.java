package com.group1.grabyourgear.supplier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.group1.grabyourgear.R;
import com.group1.grabyourgear.auth.ProfileActivity;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.UserManager;

public class SupplierDashboardActivity extends BaseActivity {

    LinearLayout lyAddProduct, lyMyProducts, lyOrders, lyStoreProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        lyAddProduct = findViewById(R.id.lyAddProduct);
        lyMyProducts = findViewById(R.id.lyMyProducts);
        lyOrders = findViewById(R.id.lyManageOrders);
        lyStoreProfile = findViewById(R.id.lyStoreProfile);

        setHeaderTitle("Supplier Dashboard");

        lyAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SupplierAddEquipmentActivity.class);
                startActivity(intent);
            }
        });

        lyMyProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SupplierMyEquipmentActivity.class);
                startActivity(intent);
            }
        });

        lyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SupplierManageOrdersActivity.class);
                startActivity(intent);
            }
        });

        lyStoreProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        if(UserManager.getInstance().isApproved()) {
            // show functionality

        } else {
            // show not approved message
            Toast.makeText(getApplicationContext(), "Account pending approval!", Toast.LENGTH_LONG).show();
        }
    }
}