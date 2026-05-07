package com.group1.grabyourgear.customer;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.core.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.group1.grabyourgear.R;
import com.group1.grabyourgear.common.AppConstants;
import com.group1.grabyourgear.common.FirebaseNodes;
import com.group1.grabyourgear.models.Booking;
import com.group1.grabyourgear.models.Equipment;
import com.group1.grabyourgear.models.Users;
import com.group1.grabyourgear.utils.BaseActivity;
import com.group1.grabyourgear.utils.DisabledDatesValidator;
import com.group1.grabyourgear.utils.EquipmentRepository;
import com.group1.grabyourgear.utils.FirebaseHelper_Bookings;
import com.group1.grabyourgear.utils.FirebaseHelper_Equipment;
import com.group1.grabyourgear.utils.UserManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomerBookingActivity extends BaseActivity {

    private Button btnBook, btnBack;
    private TextView tvDeviceName, tvTotalPrice, tvSelectedDate, tvPriceDetail;
    private MaterialCardView cardDatePicker;
    private String currentEquipmentId;
    private double pricePerDay;
    private List<Long> bookedDates = new ArrayList<>();
    private Equipment currentEquipment;
    private long selectedStart, selectedEnd;
    private double selectedTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setHeaderTitle("Booking");

        currentEquipmentId = getIntent().getStringExtra(AppConstants.IntenParamer.EQUIPMENT_ID);
        for ( Equipment eq : EquipmentRepository.getInstance().getCachedEquipment()) {
            if (eq.getId().equals(currentEquipmentId)) {
                currentEquipment = eq;
                pricePerDay = currentEquipment.getPricePerDay() * (100 - currentEquipment.getDiscount()) / 100;
                break;
            }
        }

        initViews();
        initBookedDates();
        initListeners();
    }

    private void initViews() {
        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvPriceDetail = findViewById(R.id.tvPriceDetail);

        cardDatePicker = findViewById(R.id.cardDatePicker);
        btnBook = findViewById(R.id.btnBook);
        btnBack = findViewById(R.id.btnBack);

        tvSelectedDate.setText(AppConstants.DEFAULT_DATE);
        tvDeviceName.setText(currentEquipment.getName());
    }

    private void addBookedRange(long start, long end) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            for (long t = start; t <= end; t += 24L * 60 * 60 * 1000) {
                bookedDates.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBookedDates() {
        FirebaseHelper_Bookings.loadBookingsByEquipmentId(currentEquipmentId, new FirebaseHelper_Bookings.BookingListCallback() {
            @Override
            public void onSuccess(List<Booking> bookingList) {

                // 清空旧数据
                bookedDates.clear();

                // 遍历所有 Booking，提取日期范围
                for (Booking booking : bookingList) {
                    if (booking.getStatus().equals(FirebaseNodes.BookingStatus.COMPLETED)
                    || booking.getStatus().equals(FirebaseNodes.BookingStatus.CANCELLED)
                    || booking.getStatus().equals(FirebaseNodes.BookingStatus.REJECTED) ) continue;

                    addBookedRange(booking.getStartDate(), booking.getEndDate());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CustomerBookingActivity.this,
                        "Failed to load bookings: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveBooking() {
        Users user = UserManager.getInstance().getUser();
        if (user == null){
            return;
        }

        Booking booking = new Booking(
                null,
                currentEquipment.getId(),
                user.getUid(),
                currentEquipment.getSupplierId(),
                selectedStart,
                selectedEnd,
                selectedTotalPrice,
                FirebaseNodes.BookingStatus.PENDING,
                System.currentTimeMillis(),
                0
        );

        FirebaseHelper_Bookings.createBooking(booking, new FirebaseHelper_Bookings.BookingCreateCallback() {
            @Override
            public void onSuccess() {
                // 写入成功 → 解锁
                FirebaseHelper_Equipment.unLockEquipment(currentEquipmentId, null, null);
                Toast.makeText(CustomerBookingActivity.this, "Booking successful!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                // 写入成功 → 解锁
                FirebaseHelper_Equipment.unLockEquipment(currentEquipmentId, null, null);
                Toast.makeText(CustomerBookingActivity.this, "Booking failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initListeners() {
        cardDatePicker.setOnClickListener(v -> showDateRangePicker());

        // Booking 按钮
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSelectedDate.getText().toString().equals(AppConstants.DEFAULT_DATE)) {
                    Toast.makeText(CustomerBookingActivity.this, "Please select date range", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseHelper_Equipment.lockEquipment(
                            currentEquipmentId,
                            () -> saveBooking(),   // 成功锁定 → 写入 booking
                            e -> Toast.makeText(CustomerBookingActivity.this, "Failed to lock equipment", Toast.LENGTH_SHORT).show()
                    );
                }
            };
        });

        // 返回按钮
        btnBack.setOnClickListener(v -> finish());
    }

    private void showDateRangePicker() {
        // 组合验证器：今天以前不可选 + 已预定日期不可选
        List<CalendarConstraints.DateValidator> validators = new ArrayList<>();
        validators.add(DateValidatorPointForward.now());
        validators.add(new DisabledDatesValidator(bookedDates));

        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setValidator(CompositeDateValidator.allOf(validators))
                .build();

        MaterialDatePicker<Pair<Long, Long>> picker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select Date Range")
                        .setCalendarConstraints(constraints)
                        .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            Long start = selection.first;
            Long end = selection.second;

            if (start == null || end == null) return;

            // 检查范围内是否包含已预定日期
            boolean hasDisabled = false;
            for (Long d : bookedDates) {
                if (d >= start && d <= end) {
                    hasDisabled = true;
                    break;
                }
            }

            if (hasDisabled) {
                Toast.makeText(this, "Selected range contains booked dates!", Toast.LENGTH_LONG).show();
                return;
            }

            // 显示选择的日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String startStr = sdf.format(new Date(start));
            String endStr = sdf.format(new Date(end));
            selectedStart = start;
            selectedEnd = end;

            tvSelectedDate.setText(startStr + " - " + endStr);

            // 计算天数
            long days = (end - start) / (1000 * 60 * 60 * 24) + 1;

            // 计算价格
            selectedTotalPrice = (double) (days * pricePerDay);

            tvTotalPrice.setText("$" + String.format(Locale.getDefault(), "%.2f", selectedTotalPrice));
            tvPriceDetail.setText("" + days + " days × $" + pricePerDay + "/day");

            btnBook.setEnabled(true); // 启用按钮
        });

        picker.show(getSupportFragmentManager(), "date_range_picker");
    }
}