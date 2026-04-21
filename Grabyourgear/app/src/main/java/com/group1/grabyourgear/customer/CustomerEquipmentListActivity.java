package com.group1.grabyourgear.customer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Booking;
import com.group1.grabyourgear.models.Equipment;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.CategoryRepository;
import com.group1.grabyourgear.utils.EquipmentRepository;
import com.group1.grabyourgear.utils.EquipmentView_Adapter;
import com.group1.grabyourgear.utils.FirebaseHelper_Bookings;
import com.group1.grabyourgear.utils.FirebaseHelper_Equipment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import androidx.core.util.Pair;

public class CustomerEquipmentListActivity extends BaseActivity {
    Button btnSearch, btnClear;
    EditText etLocation, etKeyword;
    Spinner spCategory;
    TextView tvDateRange;
    RecyclerView recyclerDeals;
    Long startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_equipment_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Equipments");

        tvDateRange = findViewById(R.id.tvDateRange);
        btnClear = findViewById(R.id.btnClear);
        btnSearch = findViewById(R.id.btnSearch);
        etLocation = findViewById(R.id.etLocation);
        etKeyword = findViewById(R.id.etKeyword);
        spCategory = findViewById(R.id.spCategory);
        recyclerDeals = findViewById(R.id.recyclerDeals);

        recyclerDeals.setLayoutManager(new LinearLayoutManager(this));

        tvDateRange.setText(AppConstants.DEFAULT_DATE);

        fillCategory();
        fillDateControl();

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSearch();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 关闭键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                searchEquipment();
            }
        });

        SearchByIntentParameter();
    }

    private void SearchByIntentParameter(){
        String selectedCategory = getIntent().getStringExtra(AppConstants.IntenParamer.CATEGORY);

        if (selectedCategory != null) {
            int pos = ((ArrayAdapter<String>) spCategory.getAdapter()).getPosition(selectedCategory);
            if (pos >= 0) {
                spCategory.setSelection(pos);
            }
        } else {
            spCategory.setSelection(0); // 默认 ALL
        }

        String keyword = getIntent().getStringExtra(AppConstants.IntenParamer.SEARCH_STRING);
        if (keyword != null) {
            etKeyword.setText(keyword);
        }

        searchEquipment();
    }

    private void searchEquipment() {
        // 0. get conditiony
        String location = etLocation.getText().toString().toLowerCase();
        String keyword = etKeyword.getText().toString().toLowerCase();
        String dateRange = tvDateRange.getText().toString();

        //category
        Object selectedObj = spCategory.getSelectedItem();
        String selectedCategory = selectedObj == null
                ? AppConstants.CurrentCategory.ALL
                : selectedObj.toString().toLowerCase();

        // 1. refresh equiments
        FirebaseHelper_Equipment.loadAllEquipment(new FirebaseHelper_Equipment.EquipmentListCallback() {
            @Override
            public void onSuccess(List<Equipment> equipmentList) {
                EquipmentRepository.getInstance().clearCachedEquipment();
                EquipmentRepository.getInstance().setCachedEquipment(equipmentList);

                // 2. 获取缓存的设备列表
                if (equipmentList == null) {
                    return;
                }

                // 3. 过滤
                List<Equipment> filteredList = equipmentList.stream()
                        .filter(e -> {
                            String categoryName = CategoryRepository.getInstance().getCategoryName(e.getCategoryId());
                            if (!selectedCategory.equals(AppConstants.CurrentCategory.ALL.toLowerCase())) {
                                return selectedCategory.equals(categoryName);
                            }
                            return true;
                        })
                        .filter(e -> {
                            String name = e.getName() == null ? "" : e.getName().toLowerCase();
                            String desc = e.getDescription() == null ? "" : e.getDescription().toLowerCase();
                            return keyword.isEmpty() || name.contains(keyword) || desc.contains(keyword);
                        })
                        .filter(e -> {
                            String loc = e.getLocation() == null ? "" : e.getLocation().toLowerCase();
                            return location.isEmpty() || loc.contains(location);
                        })
                        .filter(e -> FirebaseNodes.EquipmentStatus.AVAILABLE.equals(e.getStatus()))
                        .collect(Collectors.toList());

                //filter by date from booking table
                String dateShow = tvDateRange.getText().toString();
                if ( !dateShow.equals(AppConstants.DEFAULT_DATE)){
                    long userStart = startDate;
                    long userEnd = endDate;

                    // 先查询所有 bookings
                    FirebaseHelper_Bookings.loadAllBookings(new FirebaseHelper_Bookings.BookingListCallback() {
                        @Override
                        public void onSuccess(List<Booking> bookingList) {
                            List<Equipment> finalFiltered = new ArrayList<>();
                            for (Equipment e : filteredList) {
                                boolean isAvailable = true;
                                for (Booking b : bookingList) {
                                    if (!b.getEquipmentId().equals(e.getId())) continue;

                                    long bStart = b.getStartDate();
                                    long bEnd = b.getEndDate();

                                    // 日期冲突判断
                                    if (bStart <= userEnd && bEnd >= userStart) {
                                        isAvailable = false;
                                        break;
                                    }
                                }

                                if (isAvailable) {
                                    finalFiltered.add(e);
                                }
                            }

                            //显示最终过滤结果
                            EquipmentView_Adapter adapter = new EquipmentView_Adapter(CustomerEquipmentListActivity.this, finalFiltered);
                            recyclerDeals.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(CustomerEquipmentListActivity.this,"Failed to load bookings: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

                    return; // 防止提前显示未过滤的列表
                }

                // 5. 显示
                EquipmentView_Adapter adapter = new EquipmentView_Adapter(CustomerEquipmentListActivity.this, filteredList);
                recyclerDeals.setAdapter(adapter);
            }
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(CustomerEquipmentListActivity.this, "Failed," + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillDateControl() {
        tvDateRange.setOnClickListener(v -> showDateRangePicker());
    }

    private void showDateRangePicker() {
        MaterialDatePicker<Pair<Long, Long>> picker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText(AppConstants.DEFAULT_DATE)
                        .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            startDate = selection.first;
            endDate = selection.second;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String result = sdf.format(new Date(startDate))
                    + " - " + sdf.format(new Date(endDate));
            tvDateRange.setText(result);
        });

        picker.show(getSupportFragmentManager(), "date_range");
    }

    private void resetSearch() {
        spCategory.setSelection(0);
        etLocation.setText("");
        etKeyword.setText("");
        tvDateRange.setText(AppConstants.DEFAULT_DATE);
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
        spCategory.setAdapter(adapter);
        spCategory.setSelection(0);
    }

    private long parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.parse(dateStr).getTime();
        } catch (Exception e) {
            return 0;
        }
    }
}