package com.group1.grabyourgear.customer;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.auth.LoginActivity;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.models.Equipment;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.CategoryRepository;
import com.group1.grabyourgear.utils.EquipmentRepository;
import com.group1.grabyourgear.utils.FirebaseHelper_Equipment;
import com.group1.grabyourgear.utils.FirebaseHelper_Users;
import com.group1.grabyourgear.utils.UserManager;
import com.group1.grabyourgear.utils.UserPrefs;

import java.text.DecimalFormat;
import java.util.List;

public class CustomerEquipmentDetailActivity extends BaseActivity {

    TextView tvTitle, tvRating, tvLocation, tvCategory, tvSupplier, tvPrice, tvDiscount, tvDescription;
    Button btnBook, btnBack;
    ImageView imgProduct;
    String currentEquipmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_equipment_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Detail");

        btnBack = findViewById(R.id.btnBack);
        btnBook = findViewById(R.id.btnBook);
        imgProduct = findViewById(R.id.imgProduct);
        tvTitle = findViewById(R.id.tvTitle);
        tvRating = findViewById(R.id.tvRating);
        tvLocation = findViewById(R.id.tvLocation);
        tvCategory = findViewById(R.id.tvCategory);
        tvSupplier = findViewById(R.id.tvSupplier);
        tvPrice = findViewById(R.id.tvPrice);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvDescription = findViewById(R.id.tvDescription);

        currentEquipmentId = getIntent().getStringExtra(AppConstants.IntenParamer.EQUIPMENT_ID);
        Equipment equipment = null;
        for ( Equipment eq : EquipmentRepository.getInstance().getCachedEquipment()) {
            if (eq.getId().equals(currentEquipmentId)) {
                equipment = eq;
                break;
            }
        }

        if (equipment != null) {
            DecimalFormat df = new DecimalFormat("0.00");
            tvRating.setText(Html.fromHtml("<b>⭐ Rating:</b> " + equipment.getName() + " / 5", Html.FROM_HTML_MODE_LEGACY));
            tvCategory.setText(Html.fromHtml("<b>🪴 Category:</b> " + CategoryRepository.getInstance().getCategoryName(equipment.getId()), Html.FROM_HTML_MODE_LEGACY));
            tvLocation.setText(Html.fromHtml("<b>📍 Location:</b> " + equipment.getLocation(), Html.FROM_HTML_MODE_LEGACY));
            tvDiscount.setText(Html.fromHtml("<b>💵 Discount:</b> <font color='red'>" + Math.round(equipment.getDiscount()) + "%</font> OFF", Html.FROM_HTML_MODE_LEGACY));
            tvPrice.setText(Html.fromHtml("<b>💰 Price:</b> $" + df.format(equipment.getPricePerDay()) + " / day", Html.FROM_HTML_MODE_LEGACY));
            tvDescription.setText(equipment.getDescription());
            tvTitle.setText(equipment.getName());

            Glide.with(this)
                    .load(equipment.getImageUrl())
                    .placeholder(R.drawable.placeholder_general)
                    .into(imgProduct);

            FirebaseHelper_Users.loadUserInfo(equipment.getSupplierId(), new FirebaseHelper_Users.UserCallback() {
                @Override
                public void onSuccess(Users user) {
                    if (user != null) {
                    tvSupplier.setText(Html.fromHtml("<b>🧑‍🌾 Supplier:</b> " + user.getName(), Html.FROM_HTML_MODE_LEGACY));}
                }

                @Override
                public void onFailure(Exception e) {
                    tvSupplier.setText(Html.fromHtml("<b>🧑‍🌾 Supplier:</b> " + "Load failed", Html.FROM_HTML_MODE_LEGACY));
                }
            });

            btnBack.setOnClickListener(v -> {
                finish();
            });

            btnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CustomerEquipmentDetailActivity.this, CustomerBookingActivity.class);
                    intent.putExtra(AppConstants.IntenParamer.EQUIPMENT_ID, currentEquipmentId);
                    startActivity(intent);
                }
            });
        }
    }
}