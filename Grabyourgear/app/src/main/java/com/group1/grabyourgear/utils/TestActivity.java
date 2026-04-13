package com.group1.grabyourgear.utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.group1.grabyourgear.R;

public class TestActivity extends AppCompatActivity {

    private CloudinaryUploader uploader;
    private ImageView imgResult;

    /** 新 API：替代 onActivityResult */
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
        setContentView(R.layout.activity_test_image);

        imgResult = findViewById(R.id.imgResult);
        Button btnChoose = findViewById(R.id.btnChoose);

        uploader = new CloudinaryUploader(this, imagePickerLauncher);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploader.pickImage(new UploadCallback() {
                    @Override
                    public void onSuccess(String imageUrl) {
                        saveToFirebase(imageUrl);
                        showImage(imageUrl);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(TestActivity.this, "上传失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        loadLastImage();
    }

    private void saveToFirebase(String url) {
        FirebaseDatabase.getInstance()
                .getReference("testImage")
                .setValue(url);
    }

    private void loadLastImage() {
        FirebaseDatabase.getInstance()
                .getReference("testImage")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String url = snapshot.getValue(String.class);
                        showImage(url);
                    }
                });
    }

    private void showImage(String url) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.placeholder_equipment)
                .into(imgResult);
    }
}
