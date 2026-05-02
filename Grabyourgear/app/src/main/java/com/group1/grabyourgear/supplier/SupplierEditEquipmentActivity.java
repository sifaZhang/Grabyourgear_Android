package com.group1.grabyourgear.supplier;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.models.Equipment;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.CloudinaryUploader;
import com.group1.grabyourgear.utils.UploadCallback;

import java.util.Arrays;
import java.util.List;

public class SupplierEditEquipmentActivity extends BaseActivity {

    private EditText etNameEdit, etDescriptionEdit, etPriceEdit, etDiscountEdit, etLocationEdit;
    private Spinner spinnerCatEdit;
    private Button btnSaveChanges;
    private ImageView imgEquipment;
    private TextView tvUploadImg;

    private String equipmentID;
    private String currentImgURL = "";
    private Equipment currentEquipment;
    private CloudinaryUploader uploaderEquipImg;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            uploaderEquipImg.handleResult(data);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_supplier_edit_equipment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Edit Equipment Details");

        etNameEdit = findViewById(R.id.etEquipNameEdit);
        etDescriptionEdit = findViewById(R.id.etEquipDescriptionEdit);
        etPriceEdit = findViewById(R.id.etEquipPriceEdit);
        etDiscountEdit = findViewById(R.id.etEquipDiscountEdit);
        etLocationEdit = findViewById(R.id.etEquipLocationEdit);
        spinnerCatEdit = findViewById(R.id.spCategoryEditEquip);
        btnSaveChanges = findViewById(R.id.btnSaveEditEquip);
        imgEquipment = findViewById(R.id.imgEquipmentEdit);
        tvUploadImg = findViewById(R.id.tvEquipImageUploadEdit);

        btnSaveChanges.setText("Save Changes");

        fillCategory();

        uploaderEquipImg = new CloudinaryUploader(this, imagePickerLauncher);

        equipmentID = getIntent().getStringExtra(AppConstants.IntenParamer.EQUIPMENT_ID);

        if(equipmentID == null || equipmentID.isEmpty()) {
            Toast.makeText(this, "No equipment selected.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEquipment();

        tvUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploaderEquipImg.pickImage(new UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        showImage(imageUrl);
                        currentImgURL = imageUrl;
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getApplicationContext(), "Upload failed：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEquipment();
            }
        });
    }

    private void fillCategory()
    {
        List<String> category = Arrays.asList(AppConstants.CurrentCategory.ALL, AppConstants.CurrentCategory.CONSTRUCTION, AppConstants.CurrentCategory.ELECTRONIC, AppConstants.CurrentCategory.OFFICE, AppConstants.CurrentCategory.VEHICLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                category
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCatEdit.setAdapter(adapter);
        spinnerCatEdit.setSelection(0);
    }

    private void setSelectedCategory(String currentCategory) {
        for(int i = 0; i < spinnerCatEdit.getCount(); i++) {
            if(spinnerCatEdit.getItemAtPosition(i).toString().equals(currentCategory)) {
                spinnerCatEdit.setSelection(i);
                break;
            }
        }
    }

    private void loadEquipment() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("equipment")
                .child(equipmentID);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentEquipment = snapshot.getValue(Equipment.class);

                if(currentEquipment == null) {
                    Toast.makeText(SupplierEditEquipmentActivity.this, "Equipment not found",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                etNameEdit.setText(currentEquipment.getName());
                etDescriptionEdit.setText(currentEquipment.getDescription());
                etPriceEdit.setText(String.valueOf(currentEquipment.getPricePerDay()));
                etDiscountEdit.setText(String.valueOf(currentEquipment.getDiscount()));
                etLocationEdit.setText(currentEquipment.getLocation());

                setSelectedCategory(currentEquipment.getCategoryId());

                currentImgURL = currentEquipment.getImageUrl();

                Glide.with(SupplierEditEquipmentActivity.this)
                        .load(currentImgURL)
                        .placeholder(R.drawable.placeholder_general)
                        .into(imgEquipment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SupplierEditEquipmentActivity.this, "Failed to load equipment.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImage(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder_avatar)
                .into(imgEquipment);
    }

    private void updateEquipment() {
        String name = etNameEdit.getText().toString().trim();
        String description = etDescriptionEdit.getText().toString().trim();
        String priceText = etPriceEdit.getText().toString().trim();
        String discountText = etDiscountEdit.getText().toString().trim();
        String location = etLocationEdit.getText().toString().trim();

        if(name.isEmpty() || description.isEmpty() || priceText.isEmpty() || discountText.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        double discount;

        try {
            price = Double.parseDouble(priceText);
            discount = Double.parseDouble(discountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Price and discount must be numbers only.", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryId = String.valueOf(spinnerCatEdit.getSelectedItemId());

        currentEquipment.setName(name);
        currentEquipment.setDescription(description);
        currentEquipment.setPricePerDay(price);
        currentEquipment.setDiscount(discount);
        currentEquipment.setLocation(location);
        currentEquipment.setCategoryId(categoryId);
        currentEquipment.setImageUrl(currentImgURL);

        FirebaseDatabase.getInstance()
                .getReference("equipment")
                .child(equipmentID)
                .setValue(currentEquipment)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Equipment updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}